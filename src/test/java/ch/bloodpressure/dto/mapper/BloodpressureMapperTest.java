package ch.bloodpressure.dto.mapper;

import ch.bloodpressure.domain.Bloodpressure;
import ch.bloodpressure.domain.Patient;
import ch.bloodpressure.dto.BloodpressureRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class BloodpressureMapperTest {

    private BloodpressureMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(BloodpressureMapper.class);
    }

    @Test
    void should_map_entity_to_requestDto() {
        var patient = Patient.builder()
                .firstName("Max")
                .lastName("Muster")
                .email("max@example.com")
                .build();

        var bloodpressure = Bloodpressure.builder()
                .systole(120)
                .diastole(80)
                .patient(patient)
                .build();

        var bloodpressureRequestDto = mapper.toBloodpressureRequestDto(bloodpressure);

        assertThat(bloodpressureRequestDto).isNotNull();
        assertThat(bloodpressureRequestDto.patientId()).isNotNull();
        assertThat(bloodpressureRequestDto.systole()).isEqualTo(120);
        assertThat(bloodpressureRequestDto.diastole()).isEqualTo(80);
    }

    @Test
    void should_map_requestDto_to_entity() {
        var bloodpressureRequestDto =
                new BloodpressureRequestDto("p-123", 130, 85, 70, 100);

        var bloodpressure = mapper.toBloodpressure(bloodpressureRequestDto);

        assertThat(bloodpressure).isNotNull();
        assertThat(bloodpressure.getSystole()).isEqualTo(130);
        assertThat(bloodpressure.getDiastole()).isEqualTo(85);
        assertThat(bloodpressure.getPatient()).isNull();
    }

    @Test
    void should_map_entity_to_responseDto() {
        Bloodpressure bp = Bloodpressure.builder()
                .systole(140)
                .diastole(90)
                .build();

        var bloodpressureResponseDto = mapper.fromBloodpressure(bp);

        assertThat(bloodpressureResponseDto).isNotNull();
        assertThat(bloodpressureResponseDto.systole()).isEqualTo(140);
        assertThat(bloodpressureResponseDto.diastole()).isEqualTo(90);
    }

    @Test
    void should_return_null_when_input_is_null() {
        assertThat(mapper.toBloodpressureRequestDto(null)).isNull();
        assertThat(mapper.toBloodpressure( null)).isNull();
        assertThat(mapper.fromBloodpressure(null)).isNull();
    }
}
