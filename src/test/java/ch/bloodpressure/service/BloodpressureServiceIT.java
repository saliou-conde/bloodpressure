package ch.bloodpressure.service;

import ch.bloodpressure.AbstractBloodpressureIT;
import ch.bloodpressure.exception.BloodpressureNotFoundException;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BloodpressureServiceIT extends AbstractBloodpressureIT {

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Test
    void getBloodpressureById_shouldFindBloodpressure_whenBloodPressurePresent() {
        // Given
        var patient = patientPersistedTestDataBuilder.randomPatient();
        var persistedBloodpressure = bloodpressurePersistedTestDataBuilder.randomPersistedBloodpressure(patient);
        var id = persistedBloodpressure.getId();
        var systole = persistedBloodpressure.getSystole();
        var diastole = persistedBloodpressure.getDiastole();
        var heartRate = persistedBloodpressure.getHeartRate();
        var isDeleted = persistedBloodpressure.getIsDeleted();
        var createdAt = persistedBloodpressure.getCreatedAt();

        // When
        var fetchedBloodpressure = bloodpressureService.getBloodpressure(id);

        // Then
        assertThat(fetchedBloodpressure)
                .isNotNull()
                .extracting("id", "systole", "diastole", "heartRate", "isDeleted", "createdAt")
                .containsExactly(id, systole, diastole, heartRate, isDeleted, createdAt);
    }

    @Test
    void getBloodpressureById_shouldReturnException_whenBloodPressureNotPresent() {
        // Given
        var id = "non-existing-id";

        // When - Then
        assertThatThrownBy(() -> bloodpressureService.getBloodpressure(id))
                .isInstanceOf(BloodpressureNotFoundException.class)
                .hasMessageContaining(format("Cannot find bloodpressure:: No bloodpressure found with the provided Id:: %s", id));
    }

    @Test
    void updateBloodpressure_shouldModifyBloodpressure_whenValidDataGiven() {
        // Given
        var patient = patientPersistedTestDataBuilder.randomPatient();
        var bloodpressureResponseDto = bloodpressurePersistedTestDataBuilder.persistBloodpressure(patient);
        var bloodpressureToUpdate = bloodpressurePersistedTestDataBuilder.createBloodpressureRequestDto(
                patient.getId(),
                120,
                80,
                70,
                90
        );
        String id = bloodpressureResponseDto.id();

        // When
        bloodpressureService.updateBloodpressure(bloodpressureToUpdate, id);
        var bloodpressure = bloodpressureService.getBloodpressure(id);
        var systole = bloodpressure.systole();
        var diastole = bloodpressure.diastole();
        var heartRate = bloodpressure.heartRate();
        var isDeleted = bloodpressure.isDeleted();
        var createdAt = bloodpressure.createdAt();

        // Then
        assertThat(bloodpressure)
                .isNotNull()
                .extracting(
                        "id",
                        "systole",
                        "diastole",
                        "heartRate",
                        "isDeleted",
                        "createdAt")
                .containsExactly(
                        id,
                        systole,
                        diastole,
                        heartRate,
                        isDeleted,
                        createdAt);
    }
}
