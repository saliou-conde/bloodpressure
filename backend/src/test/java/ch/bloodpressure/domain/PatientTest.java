package ch.bloodpressure.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Period;

import static ch.bloodpressure.domain.Gender.MALE;
import static org.assertj.core.api.Assertions.assertThat;

class PatientTest {

    @Test
    void addBloodpressure_shouldSetPatient_whenValidDataGiven() {
        var patient = Patient.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        var bloodpressure = Bloodpressure.builder()
                .systole(120)
                .diastole(80)
                .map(93)
                .heartRate(70)
                .build();

        patient.addBloodpressure(bloodpressure);

        assertThat(patient.getBloodpressure()).contains(bloodpressure);
        assertThat(bloodpressure.getPatient()).isEqualTo(patient);
    }

    @Test
    void removeBloodpressure_shouldUnsetPatient_whenValidDataGiven() {
        var patient = Patient.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        var bloodpressure = Bloodpressure.builder()
                .systole(120)
                .diastole(80)
                .map(93)
                .heartRate(70)
                .build();

        patient.addBloodpressure(bloodpressure);
        patient.removeBloodpressure(bloodpressure);

        assertThat(patient.getBloodpressure()).doesNotContain(bloodpressure);
        assertThat(bloodpressure.getPatient()).isNull();
    }

    @Test
    void removeBloodpressure_shouldNotUnsetPatient_whenInvalidDataGiven() {
        // Given
        var patient = Patient.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .gender(MALE)
                .birthDate(java.time.LocalDate.of(1990, 1, 1))
                .build();

        var bloodpressure = Bloodpressure.builder()
                .systole(120)
                .diastole(80)
                .map(93)
                .heartRate(70)
                .build();

        // When
        patient.addBloodpressure(bloodpressure);
        patient.removeBloodpressure(null);

        // Then
        assertThat(patient.getBloodpressure()).contains(bloodpressure);
    }

    @Test
    void removeBloodpressure_shouldNotUnsetPatient_whenIncorrectDataGiven() {
        // Given
        var patient = Patient.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .gender(MALE)
                .birthDate(java.time.LocalDate.of(1990, 1, 1))
                .build();

        var bloodpressure1 = Bloodpressure.builder()
                .systole(120)
                .diastole(80)
                .map(93)
                .heartRate(70)
                .build();

        var bloodpressure2 = Bloodpressure.builder()
                .systole(130)
                .diastole(85)
                .map(100)
                .heartRate(75)
                .build();

        // When
        patient.addBloodpressure(bloodpressure1);
        patient.removeBloodpressure(bloodpressure2);

        // Then
        assertThat(patient.getBloodpressure()).contains(bloodpressure1);
        assertThat(patient.getBloodpressure()).doesNotContain(bloodpressure2);
    }

    @Test
    void getAge_shouldReturnCorrectAge_whenBirthDateIsSet() {
        // Given
        var patient = Patient.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .gender(MALE)
                .birthDate(java.time.LocalDate.of(1990, 1, 1))
                .build();

        // When
        var actualAge = patient.getAge();

        // Then
        var expectedAge = Period.between(patient.getBirthDate(), LocalDate.now()).getYears();
        assertThat(actualAge).isEqualTo(expectedAge);
    }

    @Test
    void getAge_shouldReturnZero_whenBirthDateIsNull() {
        // Given
        var patient = Patient.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .gender(MALE)
                .build();

        // When
        var age = patient.getAge();

        // Then
        assertThat(age).isEqualTo(0);
    }
}
