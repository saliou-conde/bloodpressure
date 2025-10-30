package ch.bloodpressure.controller;

import ch.bloodpressure.domain.Gender;
import ch.bloodpressure.dto.AddressDto;
import ch.bloodpressure.dto.PatientRequestDto;
import ch.bloodpressure.dto.PatientResponseDto;
import ch.bloodpressure.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ch.bloodpressure.domain.Gender.FEMALE;
import static ch.bloodpressure.domain.Gender.MALE;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPatients_shouldReturnPatientsList_whenPatientsExist() {
        // Given
        var address = new AddressDto("Main St", "City", "State", "12345");
        var expectedPatients = List.of(
                createResponse("Marx", "Mustermann", MALE, "1980-01-01", address),
                createResponse("John", "Doe", MALE, "1970-01-01", address),
                createResponse("John", "Smitt", MALE, "1970-01-01", address),
                createResponse("Erika", "Mustermann", FEMALE, "1990-01-01", address),
                createResponse("Jane", "Doe", FEMALE, "1990-01-01", address)
        );
        var expectedTotal = expectedPatients.size();
        var expectedMaleCount = 3;
        var expectedFemaleCount = 2;

        when(patientService.getPatients()).thenReturn(expectedPatients);

        // When
        var responseEntity = patientController.getPatients();

        // Then
        assertAll(
                () -> assertThat(responseEntity.getStatusCode()).isEqualTo(OK),
                () -> assertThat(responseEntity.getBody()).isNotNull(),
                () -> assertThat(responseEntity.getBody()).hasSize(expectedTotal)
        );

        Map<Gender, Long> genderCount = responseEntity.getBody().stream()
                .collect(groupingBy(PatientResponseDto::gender, counting()));
        assertAll(
                () -> assertThat(genderCount.get(MALE)).isEqualTo(expectedMaleCount),
                () -> assertThat(genderCount.get(FEMALE)).isEqualTo(expectedFemaleCount)
        );

        // Verify interactions
        verify(patientService).getPatients();
        verifyNoMoreInteractions(patientService);
    }

    @Test
    void createPatient_shouldInsertPatient_whenValidDataGiven() {
        // Given
        var request = createRequest();
        var address = new AddressDto("Main St", "City", "State", "12345");
        var response = createResponse("Marx", "Mustermann", MALE, "1980-01-01", address);
        when(patientService.createPatient(request)).thenReturn(response);

        // When
        var responseEntity = patientController.createPatient(request);

        // Then
        assertAll(
                () -> assertThat(responseEntity.getStatusCode()).isEqualTo(CREATED),
                () -> assertThat(responseEntity.getBody()).isEqualTo(response)
        );

        // Verify interactions
        verify(patientService).createPatient(request);
        verifyNoMoreInteractions(patientService);
    }

    @Test
    void getPatient_shouldReturnPatientById_whenPatientExists() {
        // Given
        var id = "1";
        var address = new AddressDto("Main St", "City", "State", "12345");
        var expectedPatient = createResponse("Marx", "Mustermann", MALE, "1980-01-01", address);
        when(patientService.getPatient(id)).thenReturn(expectedPatient);

        // When
        var responseEntity = patientController.getPatient(id);

        // Then
        assertAll(
                () -> assertThat(responseEntity.getStatusCode()).isEqualTo(OK),
                () -> assertThat(responseEntity.getBody()).isEqualTo(expectedPatient)
        );

        // Verify interactions
        verify(patientService).getPatient(id);
        verifyNoMoreInteractions(patientService);
    }

    @Test
    void deletePatient_shouldPatientById_whenPatientExists() {
        // Given
        var patientId = UUID.randomUUID().toString();

        // When
        var responseEntity = patientController.deletePatient(patientId);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(ACCEPTED);

        // Verify interactions
        verify(patientService).deletePatient(patientId);
        verifyNoMoreInteractions(patientService);
    }

    @Test
    void updatePatient_shouldModifyPatient_whenPatientExists() {
        // Given
        var id = UUID.randomUUID().toString();
        var request = createRequest();

        // When
        var responseEntity = patientController.updatePatient(id, request);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(ACCEPTED);

        // Verify interactions
        verify(patientService).updatePatient(request, id);
        verifyNoMoreInteractions(patientService);
    }

    private PatientRequestDto createRequest() {
        var address = new AddressDto("Main St", "City", "State", "12345");
        return new PatientRequestDto("Max", "Mustermann", "max@example.com", MALE, "1990-01-01", address);
    }

    private PatientResponseDto createResponse(String firstname, String lastname, Gender gender, String birthDate, AddressDto address) {
        return new PatientResponseDto(
                UUID.randomUUID().toString(),
                firstname,
                lastname,
                firstname.concat("-"+lastname).concat("@example.com"),
                gender,
                birthDate,
                false,
                List.of(),
                address);
    }
}