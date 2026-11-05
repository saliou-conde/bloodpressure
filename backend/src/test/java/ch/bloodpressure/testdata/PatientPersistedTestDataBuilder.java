package ch.bloodpressure.testdata;

import ch.bloodpressure.domain.Bloodpressure;
import ch.bloodpressure.domain.Gender;
import ch.bloodpressure.domain.Patient;
import ch.bloodpressure.dto.AddressDto;
import ch.bloodpressure.dto.PatientRequestDto;
import ch.bloodpressure.dto.PatientResponseDto;
import ch.bloodpressure.dto.mapper.PatientMapper;
import ch.bloodpressure.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.instancio.Instancio;
import org.instancio.Select;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.random;
import static java.util.UUID.randomUUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PatientPersistedTestDataBuilder {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientResponseDto persist(PatientRequestDto requestDto) {
        var patient = patientMapper.toPatientFromPatientRequestDto(requestDto);
        var persisted = patientRepository.saveAndFlush(patient);
        return patientMapper.fromPatientWithoutMeasurements(persisted);
    }

    public PatientRequestDto randomPatientRequestDto() {
        var gender = Gender.values()[(int) (Math.random() * Gender.values().length)];

        var address = new AddressDto(
                "Teststraße " + (int) (Math.random() * 100),
                "Berlin",
                "Berlin",
                "12345"
        );
        var patientRequestDto = new PatientRequestDto(
                randomFirstName(),
                randomLastName(),
                randomEmail(),
                gender,
                "1987-09-22", // gültiges ISO-Datum
                address
        );
        log.info("PatientRequestDto: {}", patientRequestDto);

        return patientRequestDto;
    }

    public PatientRequestDto createPatientRequestDto(String firstName, String lastName, String email) {
        return Instancio.of(PatientRequestDto.class)
                .set(Select.field(PatientRequestDto::firstName), firstName)
                .set(Select.field(PatientRequestDto::lastName), lastName)
                .set(Select.field(PatientRequestDto::email), email)
                .create();
    }

    public PatientResponseDto randomPersistedPatient() {
        var requestDto = randomPatientRequestDto();
        return persist(requestDto);
    }

    public Patient persistedPatientWithBloodpressureMeasurements(Patient patient) {
        return patientRepository.saveAndFlush(patient);
    }

    public Patient persistedPatientWithBloodpressureMeasurements(PatientRequestDto requestDto,
                                                                 List<Bloodpressure> measurements) {
        var patient = patientMapper.toPatientFromPatientRequestDto(requestDto);
        measurements.forEach(patient::addBloodpressure);
        return patientRepository.saveAndFlush(patient);
    }

    public Patient removeBloodpressureeasurementsFromPatient(Patient patient,
                                                             List<Bloodpressure> measurements) {
        // Generate a copy to avoid modifying the list while iterating over it
        new ArrayList<>(measurements).forEach(patient::removeBloodpressure);
        return patientRepository.saveAndFlush(patient);
    }

    public Patient randomPatient() {
        return patientMapper.toPatientFromPatientRequestDto(randomPatientRequestDto());
    }

    private String randomFirstName() {
        var names = new String[]{"Marx", "Anna", "John", "Maria", "David", "Emma"};
        var idx = (int) (random() * names.length);
        return names[idx];
    }

    private String randomLastName() {
        var names = new String[]{"Nero", "Smith", "Doe", "Müller", "Brown", "Garcia"};
        var idx = (int) (random() * names.length);
        return names[idx];
    }

    private String randomEmail() {
        var name = randomFirstName().toLowerCase() + "." + randomLastName().toLowerCase();
        var user = name+ randomUUID().toString().substring(0, 8);
        return user + "@gmail.com";
    }
}
