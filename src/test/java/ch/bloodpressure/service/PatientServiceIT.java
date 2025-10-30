package ch.bloodpressure.service;

import ch.bloodpressure.AbstractBloodpressureIT;
import ch.bloodpressure.domain.Bloodpressure;
import ch.bloodpressure.domain.Patient;
import ch.bloodpressure.exception.EmailAlreadyExistException;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PatientServiceIT extends AbstractBloodpressureIT {

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Test
    void createPatient_shouldInsertPatient_whenValidDataGiven() {
        // Given
        var patient = patientPersistedTestDataBuilder.randomPatient();
        var firstName = patient.getFirstName();
        var lastName = patient.getLastName();
        var email = patient.getEmail();
        List<Bloodpressure> bloodpressure = List.of(
                bloodpressurePersistedTestDataBuilder.randomBloodpressure(),
                bloodpressurePersistedTestDataBuilder.randomBloodpressure()
        );
        bloodpressure.forEach(patient::addBloodpressure);

        // When
        var createdPatient = patientPersistedTestDataBuilder.persistedPatientWithBloodpressureMeasurements(patient);
        var bloodpressureMeasurements = createdPatient.getBloodpressure();

        // Then
        assertThat(createdPatient)
                .isNotNull()
                .extracting("firstName", "lastName", "email")
                .containsExactly(
                        firstName,
                        lastName,
                        email
                );

        assertThat(bloodpressureMeasurements)
                .isNotNull()
                .hasSize(2)
                .containsExactlyElementsOf(bloodpressure);

        assertThat(bloodpressureMeasurements.getFirst())
                .extracting("systole", "diastole", "heartRate", "map")
                .containsExactly(
                        bloodpressure.getFirst().getSystole(),
                        bloodpressure.getFirst().getDiastole(),
                        bloodpressure.getFirst().getHeartRate(),
                        bloodpressure.getFirst().getMap()
                );
    }

    @Test
    void createPatient_shouldReturnException_whenEmailAlreadyExists() {
        // Given
        var patientRequestDto = patientPersistedTestDataBuilder.randomPatientRequestDto();
        patientService.createPatient(patientRequestDto);

        // When - Then
        assertThatThrownBy(() -> patientService.createPatient(patientRequestDto))
                .isInstanceOf(EmailAlreadyExistException.class)
                .hasMessageContaining(format("Patient with email %s already exists.", patientRequestDto.email()));
    }

    @Test
    void getPatientById_shouldFindPatient_withoutBloodpressureMeasurements() {
        // Given
        var persistedPatient = patientPersistedTestDataBuilder.randomPersistedPatient();


        // When
        var fetchedPatient = patientService.getPatient(persistedPatient.id());

        // Then
        assertThat(fetchedPatient)
                .isNotNull()
                .extracting("id", "firstName", "lastName", "email")
                .containsExactly(
                        persistedPatient.id(),
                        persistedPatient.firstName(),
                        persistedPatient.lastName(),
                        persistedPatient.email()
                );

        assertThat(fetchedPatient.bloodpressureRequestDtoList())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void getPatientById_shouldFindPatient_withBloodpressureMeasurements() {
        // Given
        var patient = createPatient();
        var measurements = patient.getBloodpressure();

        // When
        var fetchedPatient = patientService.getPatient(patient.getId());

        // Then
        assertThat(fetchedPatient)
                .isNotNull()
                .extracting("id", "firstName", "lastName", "email")
                .containsExactly(
                        patient.getId(),
                        patient.getFirstName(),
                        patient.getLastName(),
                        patient.getEmail()
                );

        assertThat(fetchedPatient.bloodpressureRequestDtoList())
                .hasSize(measurements.size())
                .zipSatisfy(measurements, (fetched, expected) -> {
                    assertThat(fetched.patientId()).isEqualTo(patient.getId());
                    assertThat(fetched.systole()).isEqualTo(expected.getSystole());
                    assertThat(fetched.diastole()).isEqualTo(expected.getDiastole());
                    assertThat(fetched.heartRate()).isEqualTo(expected.getHeartRate());
                });
    }

    @Test
    void getPatientById_shouldReturnPatient_withRemovedBloodpressureMeasurements() {
        // Given
        var patient = createPatient();
        var measurements = patient.getBloodpressure();
        var patientWithoutMeasurements = patientPersistedTestDataBuilder
                .removeBloodpressureeasurementsFromPatient(patient, measurements);

        // When
        var fetchedPatient = patientService.getPatient(patientWithoutMeasurements.getId());

        // Then
        assertThat(fetchedPatient)
                .isNotNull()
                .extracting("id", "firstName", "lastName", "email")
                .containsExactly(
                        patientWithoutMeasurements.getId(),
                        patientWithoutMeasurements.getFirstName(),
                        patientWithoutMeasurements.getLastName(),
                        patientWithoutMeasurements.getEmail()
                );

        assertThat(fetchedPatient.bloodpressureRequestDtoList())
                .hasSize(0);

    }

    private Patient createPatient() {
        var patientRequestDto = patientPersistedTestDataBuilder.randomPatientRequestDto();
        var measurements = List.of(
                bloodpressurePersistedTestDataBuilder.randomBloodpressure(),
                bloodpressurePersistedTestDataBuilder.randomBloodpressure()
        );
        return patientPersistedTestDataBuilder.persistedPatientWithBloodpressureMeasurements(patientRequestDto,
                measurements);

    }
}
