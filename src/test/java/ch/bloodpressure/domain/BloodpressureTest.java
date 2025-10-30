package ch.bloodpressure.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BloodpressureTest {

    @Test
    void equalsAndHashCode_shouldBeCorrect() {
        var bloodpressure1 = Bloodpressure.builder()
                .systole(120)
                .diastole(80)
                .map(93)
                .heartRate(70)
                .build();

        var bloodpressure2 = Bloodpressure.builder()
                .systole(120)
                .diastole(80)
                .map(93)
                .heartRate(70)
                .build();

        bloodpressure1.setId("1");
        bloodpressure2.setId("1");

        assertThat(bloodpressure1).isEqualTo(bloodpressure2);
        assertThat(bloodpressure1.hashCode()).isEqualTo(bloodpressure2.hashCode());
    }

    @Test
    void equals_shouldReturnFalseForDifferentIds() {
        var bloodpressure1 = Bloodpressure.builder()
                .systole(120).diastole(80).map(93).heartRate(70).build();
        var bp2 = Bloodpressure.builder()
                .systole(120).diastole(80).map(93).heartRate(70).build();

        bloodpressure1.setId("1");
        bp2.setId("2");

        assertThat(bloodpressure1).isNotEqualTo(bp2);
    }
}
