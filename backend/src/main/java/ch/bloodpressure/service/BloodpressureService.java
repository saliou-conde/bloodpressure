package ch.bloodpressure.service;

import ch.bloodpressure.dto.BloodpressureRequestDto;
import ch.bloodpressure.dto.BloodpressureResponseDto;

import java.util.List;

public interface BloodpressureService {
    List<BloodpressureResponseDto> getBloodpressure();
    List<BloodpressureResponseDto> getBloodpressureMeasurementsByPatientId(String patientId);
    BloodpressureResponseDto addBloodpressure(BloodpressureRequestDto requestDto);
    BloodpressureResponseDto getBloodpressure(String id);
    void updateBloodpressure(BloodpressureRequestDto requestDto, String id);
    void deleteBloodpressure(String id);
}
