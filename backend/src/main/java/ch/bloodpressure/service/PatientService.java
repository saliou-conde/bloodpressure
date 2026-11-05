package ch.bloodpressure.service;

import ch.bloodpressure.domain.Patient;
import ch.bloodpressure.dto.PatientRequestDto;
import ch.bloodpressure.dto.PatientResponseDto;

import java.util.List;

public interface PatientService {
    List<PatientResponseDto> getPatients();
    PatientResponseDto getPatient(String id);
    PatientResponseDto createPatient(PatientRequestDto patient);
    void deletePatient(String id);
    void updatePatient(PatientRequestDto patient, String id);
    void deleteAll();
    Patient getPatientById(String id, String errorMessage);
}
