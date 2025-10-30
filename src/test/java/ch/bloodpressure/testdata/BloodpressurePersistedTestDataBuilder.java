package ch.bloodpressure.testdata;

import ch.bloodpressure.domain.Bloodpressure;
import ch.bloodpressure.domain.Patient;
import ch.bloodpressure.dto.BloodpressureRequestDto;
import ch.bloodpressure.dto.BloodpressureResponseDto;
import ch.bloodpressure.dto.mapper.BloodpressureMapper;
import ch.bloodpressure.repository.BloodpressureRepository;
import ch.bloodpressure.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Select;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BloodpressurePersistedTestDataBuilder {

    private final BloodpressureRepository bloodpressureRepository;
    private final PatientRepository patientRepository;
    private final BloodpressureMapper bloodpressureMapper;

    public BloodpressureRequestDto createBloodpressureRequestDto(String patientId, Integer systole,
                                                                 Integer diastole, Integer heartRate, Integer map) {
        return Instancio.of(BloodpressureRequestDto.class)
                .set(Select.field(BloodpressureRequestDto::patientId), patientId)
                .set(Select.field(BloodpressureRequestDto::systole), systole)
                .set(Select.field(BloodpressureRequestDto::diastole), diastole)
                .set(Select.field(BloodpressureRequestDto::heartRate), heartRate)
                .set(Select.field(BloodpressureRequestDto::map), map)
                .create();
    }

    public BloodpressureRequestDto randomBloodpressureRequestDto() {
        var systole = (int) (90 + Math.random() * 60);
        var diastole = (int) (60 + Math.random() * 40);
        var heartRate = (int) (60 + Math.random() * 40);
        var map = (systole + 2*diastole)/3;
        return Instancio.of(BloodpressureRequestDto.class)
                .set(Select.field(BloodpressureRequestDto::patientId), UUID.randomUUID().toString())
                .set(Select.field(BloodpressureRequestDto::systole), systole)
                .set(Select.field(BloodpressureRequestDto::diastole), diastole)
                .set(Select.field(BloodpressureRequestDto::heartRate), heartRate)
                .set(Select.field(BloodpressureRequestDto::map), map)
                .create();
    }

    public Bloodpressure randomBloodpressure() {
        var requestDto = randomBloodpressureRequestDto();
        return bloodpressureMapper.toBloodpressure(requestDto);
    }

    public Bloodpressure randomPersistedBloodpressure(Patient patient) {
        var bloodpressure = randomBloodpressure();
        return persistBloodpressure(bloodpressure, patient);
    }

    public Bloodpressure persistBloodpressure(Bloodpressure bloodpressure, Patient patient) {
        var savedPatient = patientRepository.save(patient);
        bloodpressure.setPatient(savedPatient);
        return bloodpressureRepository.saveAndFlush(bloodpressure);
    }

    public BloodpressureResponseDto persistBloodpressure(BloodpressureRequestDto requestDto, Patient patient) {
        var bloodpressure = bloodpressureMapper.toBloodpressure(requestDto);
        var persistedBloodpressure = persistBloodpressure(bloodpressure, patient);
        return bloodpressureMapper.fromBloodpressure(persistedBloodpressure);
    }

    public BloodpressureResponseDto persistBloodpressure(Patient patient) {
        return persistBloodpressure(randomBloodpressureRequestDto(), patient);
    }
}
