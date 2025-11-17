package ch.bloodpressure.testdata;

import ch.bloodpressure.domain.*;
import ch.bloodpressure.dto.*;
import ch.bloodpressure.dto.mapper.PatientMapper;
import ch.bloodpressure.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.instancio.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PatientPersistedTestDataBuilder {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientResponseDto persist(PatientRequestDto requestDto) {
        var patient = patientMapper.toPatientFromPatientRequestDto(requestDto);
        var persisted = patientRepository.saveAndFlush(patient);
        return patientMapper.fromPatientWithoutMeasurements(persisted);
    }

    public PatientRequestDto randomPatientRequestDto() {
        return Instancio.of(PatientRequestDto.class)
                .set(Select.field(PatientRequestDto::firstName), randomFirstName())
                .set(Select.field(PatientRequestDto::lastName), randomLastName())
                .set(Select.field(PatientRequestDto::email), randomEmail())
                .create();
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
        var idx = (int) (Math.random() * names.length);
        return names[idx];
    }

    private String randomLastName() {
        var names = new String[]{"Nero", "Smith", "Doe", "Müller", "Brown", "Garcia"};
        var idx = (int) (Math.random() * names.length);
        return names[idx];
    }

    private String randomEmail() {
        var name = randomFirstName().toLowerCase() + "." + randomLastName().toLowerCase();
        var user = name+ UUID.randomUUID().toString().substring(0, 8);
        return user + "@gmail.com";
    }
}
