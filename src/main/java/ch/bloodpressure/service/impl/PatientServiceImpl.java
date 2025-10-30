package ch.bloodpressure.service.impl;

import ch.bloodpressure.domain.Bloodpressure;
import ch.bloodpressure.domain.Patient;
import ch.bloodpressure.dto.AddressDto;
import ch.bloodpressure.dto.BloodpressureRequestDto;
import ch.bloodpressure.dto.PatientRequestDto;
import ch.bloodpressure.dto.PatientResponseDto;
import ch.bloodpressure.dto.mapper.PatientMapper;
import ch.bloodpressure.exception.EmailAlreadyExistException;
import ch.bloodpressure.exception.PatientNotFoundException;
import ch.bloodpressure.repository.BloodpressureRepository;
import ch.bloodpressure.repository.PatientRepository;
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
public class PatientServiceImpl implements PatientService {

    private static final String NO_PATIENT_FOUND_WITH_THE_PROVIDED_ID = "No patient found with the provided Id:: %s";

    private final PatientRepository patientRepository;
    private final BloodpressureRepository bloodpressureRepository;
    private final PatientMapper patientMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDto> getPatients() {
        log.info("Starting getPatients()...");
        var patients = patientRepository.findAllPatientsOnly();
        if (patients.isEmpty()) {
            log.warn("No patients found in the database.");
            throw new PatientNotFoundException("No patients found in the database.");
        }
        var patientResponses = patients.stream()
                .map(patientMapper::fromPatientWithoutMeasurements)
                .toList();
        log.info("Found {} patients in the database.", patientResponses.size());
        log.info("Started getPatients() successfully.");
        return patientResponses;
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponseDto getPatient(final String id) {
        log.info("Starting getPatientCorrect(String id)...");
        var patient = getPatientById(id, "Cannot get patient:: ");
        log.info("Found patient with ID: {}", patient.getId());
        var patientResponse = generateResponseDto(patient, patient.getBloodpressure());
        log.info("Started getPatientCorrect(String id) successfully.");
        return patientResponse;
    }

    @Override
    public PatientResponseDto createPatient(final PatientRequestDto patientRequestDto) {
        log.info("Starting createPatient(PatientRequestDto patientRequestDto)...");
        var trimmedEmail = patientRequestDto.email().trim();
        var exists = patientRepository.existsByEmailIgnoreCase(trimmedEmail);
        if (exists) {
            log.warn("Patient with email {} already exists.", trimmedEmail);
            throw new EmailAlreadyExistException(format("Patient with email %s already exists.", trimmedEmail));
        }
        var patient = patientMapper.toPatientFromPatientRequestDto(patientRequestDto);
        patientRepository.save(patient);
        log.info("Created patient with ID: {}", patient.getId());
        var result = generateResponseDto(patient);
        log.info("Started createPatient(PatientRequestDto patientRequestDto) successfully.");
        return result;
    }

    @Override
    public void deletePatient(final String id) {
        log.info("Starting deletePatient(String id)...");
        var patient = getPatientById(id, "Cannot delete patient:: ");
        var bloodpressure = patient.getBloodpressure();
        bloodpressure.forEach(bp -> bp.setIsDeleted(true));
        patient.setBloodpressure(bloodpressure);
        patient.setIsDeleted(true);
        patientRepository.save(patient);
        log.info("Deleted patient with ID: {}", patient.getId());
        log.info("Started deletePatient(String id) successfully.");
    }

    @Override
    public void updatePatient(final PatientRequestDto patient, String id) {
        log.info("Starting updatePatient(PatientRequestDto patient, String id)...");
        var existingPatient = getPatientById(id, "Cannot update patient:: ");
        mergePatient(existingPatient, patient);
        patientRepository.save(existingPatient);
        log.info("Updated patient with ID: {}", existingPatient.getId());
        log.info("Started updatePatient(PatientRequestDto patient, String id) successfully.");
    }

    @Override
    @Transactional
    public void deleteAll() {
        log.info("Starting deleteAll()...");
        bloodpressureRepository.markAllBloodpressureAsDeleted();
        patientRepository.markAllPatientsAsDeleted();
        log.info("Started deleteAll() successfully.");
    }

    public Patient getPatientById(final String id, final String errorMessage) {
        return patientRepository.findPatientByIdWithMeasurements(id)
                .orElseThrow(() -> new PatientNotFoundException(format(errorMessage.concat(NO_PATIENT_FOUND_WITH_THE_PROVIDED_ID), id)));
    }

    /***
     * This is a helper method
    public AddressDto createAddress(AddressDto addressDto) {
        log.info("Starting createAddress(AddressDto addressDto)...");
        var address = addressRepository.save(new ch.bloodpressure.domain.Address(
                addressDto.street(),
                addressDto.city(),
                addressDto.state(),
                addressDto.zipCode()
        ));
        var result = addressMapper.toAddressDto(address);
        log.info("Started createAddress(AddressDto addressDto) successfully.");
        return result;
    }

    **/

    private void mergePatient(final Patient existingPatient, final PatientRequestDto patient) {
        if (patient.firstName() != null) {
            existingPatient.setFirstName(patient.firstName());
        }
        if (patient.lastName() != null) {
            existingPatient.setLastName(patient.lastName());
        }
        if (patient.email() != null) {
            existingPatient.setEmail(patient.email());
        }
    }

    private PatientResponseDto generateResponseDto(final Patient patient, final List<Bloodpressure> measurements) {
        return new PatientResponseDto(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getEmail(),
                patient.getGender(),
                patient.getBirthDate().toString(),
                patient.getIsDeleted(),
                measurements.stream()
                        .map(bp -> new BloodpressureRequestDto(
                                patient.getId(),
                                bp.getSystole(),
                                bp.getDiastole(),
                                bp.getHeartRate(),
                                bp.getMap()
                        ))
                        .toList(),
                new AddressDto(
                        patient.getAddress().getStreet(),
                        patient.getAddress().getCity(),
                        patient.getAddress().getState(),
                        patient.getAddress().getZipCode())
        );
    }

    private PatientResponseDto generateResponseDto(final Patient patient) {
        return new PatientResponseDto(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getEmail(),
                patient.getGender(),
                patient.getBirthDate().toString(),
                patient.getIsDeleted(),
                List.of(),
                new AddressDto(
                        patient.getAddress().getStreet(),
                        patient.getAddress().getCity(),
                        patient.getAddress().getState(),
                        patient.getAddress().getZipCode())
        );
    }
}
