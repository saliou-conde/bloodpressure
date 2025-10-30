package ch.bloodpressure.service.impl;

import ch.bloodpressure.domain.Bloodpressure;
import ch.bloodpressure.dto.BloodpressureRequestDto;
import ch.bloodpressure.dto.BloodpressureResponseDto;
import ch.bloodpressure.dto.mapper.BloodpressureMapper;
import ch.bloodpressure.exception.BloodpressureNotFoundException;
import ch.bloodpressure.repository.BloodpressureRepository;
import ch.bloodpressure.service.BloodpressureService;
import ch.bloodpressure.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class BloodpressureServiceImpl implements BloodpressureService {

    private static final String NO_BLOODPRESSURE_FOUND_WITH_THE_PROVIDED_ID = "No bloodpressure found with the provided Id:: %s";

    private final BloodpressureRepository bloodpressureRepository;
    private final BloodpressureMapper bloodpressureMapper;
    private final PatientService patientService;

    @Override
    public List<BloodpressureResponseDto> getBloodpressure() {
        return bloodpressureRepository.findAllByIsDeletedFalse()
                .stream()
                .peek(bp -> log.debug("Including Bloodpressure id: {}", bp.getId()))
                .map(bloodpressureMapper::fromBloodpressure)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloodpressureResponseDto> getBloodpressureMeasurementsByPatientId(final String patientId) {
        var bloodpressureMeasurements = bloodpressureRepository.findBloodpressureByPatientId(patientId);
        if (!bloodpressureMeasurements.isEmpty()) {
            return bloodpressureMeasurements.stream()
                    .map(bloodpressureMapper::fromBloodpressure)
                    .toList();
        }
        return List.of();
    }

    @Override
    @Transactional
    public BloodpressureResponseDto addBloodpressure(BloodpressureRequestDto requestDto) {
        log.info("Start addBloodpressure( {} )...", requestDto);
        String patientId = requestDto.patientId();
        var patient = patientService.getPatientById(patientId, "Cannot get patient:: ");
        Bloodpressure bloodpressure = bloodpressureMapper.toBloodpressure(requestDto);
        bloodpressure.setPatient(patient);
        bloodpressure.setMap(calculateMap(bloodpressure.getSystole(), bloodpressure.getDiastole()));
        Bloodpressure saved = bloodpressureRepository.save(bloodpressure);
        log.info("Bloodpressure created with id:: {}", bloodpressure.getId());
        log.info("addBloodpressure finished successfully");
        return bloodpressureMapper.fromBloodpressure(saved);
    }

    @Override
    public void updateBloodpressure(BloodpressureRequestDto requestDto, String id) {
        log.info("Start updateBloodpressure({})...", requestDto);
        var bloodpressure =
                getBloodPressureByIdOrThrow(id, "Cannot update bloodpressure:: ");
        mergeBloodpressure(bloodpressure, requestDto);
        var result = bloodpressureRepository.save(bloodpressure);
        log.info("Bloodpressure updated with id:: {}", result.getId());
        log.info("End updateBloodpressure(BloodpressureRequestDto request) successfully.");
    }

    @Override
    public BloodpressureResponseDto getBloodpressure(String id) {
        log.info("Start getBloodpressure({})...", id);
        var bloodpressure = getBloodPressureByIdOrThrow(id, "Cannot find bloodpressure:: ");
        var result = bloodpressureMapper.fromBloodpressure(bloodpressure);
        log.info("Found bloodpressure with ID: {}", bloodpressure.getId());
        log.info("End getBloodpressure({}) successfully.", id);
        return result;
    }

    @Override
    public void deleteBloodpressure(String id) {
        log.info("Start deleteBloodpressure(String id)...");
        var bloodpressure = getBloodPressureByIdOrThrow(id, "Cannot delete bloodpressure:: ");
        bloodpressure.setIsDeleted(true);
        bloodpressureRepository.save(bloodpressure);
        log.info("Bloodpressure deleted with id:: {}", bloodpressure.getId());
        log.info("End deleteBloodpressure(String id) successfully.");
    }

    private Bloodpressure getBloodPressureByIdOrThrow(String id, String errorMessage) {
        var optionalBloodpressure = bloodpressureRepository.findByIdAndIsDeletedFalse(id);
        return optionalBloodpressure.orElseThrow(() -> new BloodpressureNotFoundException(
                format(errorMessage.concat(NO_BLOODPRESSURE_FOUND_WITH_THE_PROVIDED_ID), id)));
    }

    private void mergeBloodpressure(Bloodpressure bloodpressure, BloodpressureRequestDto request) {
        if(request.systole() != null) {
            bloodpressure .setSystole(request.systole());
        }
        if(request.diastole() != null) {
            bloodpressure .setDiastole(request.diastole());
        }
        if(request.heartRate() != null) {
            bloodpressure .setHeartRate(request.heartRate());
        }
        if(request.map() != null) {
            bloodpressure .setMap(request.map());
        }
    }

    private int calculateMap(int systole, int diastole) {
        double result = Math.round((2.0 * diastole + systole) / 3.0);
        return (int) result;
    }
}
