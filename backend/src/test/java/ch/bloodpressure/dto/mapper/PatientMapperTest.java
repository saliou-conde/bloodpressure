package ch.bloodpressure.dto.mapper;

import ch.bloodpressure.domain.Address;
import ch.bloodpressure.domain.Bloodpressure;
import ch.bloodpressure.domain.Patient;
import ch.bloodpressure.dto.AddressDto;
import ch.bloodpressure.dto.PatientRequestDto;
import ch.bloodpressure.dto.PatientResponseDto;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static ch.bloodpressure.domain.Gender.MALE;
import static org.assertj.core.api.Assertions.assertThat;

class PatientMapperTest {

    private PatientMapper patientMapper;
    private final AddressDto address = new AddressDto("Main St", "City", "State", "12345");

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        patientMapper = new PatientMapperImpl();

        BloodpressureMapper bloodpressureMapper = Mappers.getMapper(BloodpressureMapper.class);

        var field = PatientMapperImpl.class.getDeclaredField("bloodpressureMapper");
        field.setAccessible(true);
        field.set(patientMapper, bloodpressureMapper);
    }

    @Test
    void should_map_patient_to_responseDto_with_measurements() {
        // given
        var bp = new Bloodpressure();
        bp.setId(UUID.randomUUID().toString());
        bp.setSystole(120);
        bp.setDiastole(80);
        bp.setIsDeleted(false);

        var patient = new Patient();
        patient.setId(UUID.randomUUID().toString());
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setEmail("john.doe@example.com");
        patient.setIsDeleted(false);
        patient.setBloodpressure(List.of(bp));

        // when
        var patientResponseDto = patientMapper.fromPatientWithMeasurements(patient);

        // then
        assertThat(patientResponseDto).isNotNull();
        assertThat(patientResponseDto.firstName()).isEqualTo("John");
        assertThat(patientResponseDto.bloodpressureRequestDtoList()).hasSize(1);
        assertThat(patientResponseDto.bloodpressureRequestDtoList().getFirst().systole()).isEqualTo(120);
    }

    @Test
    void should_map_fromPatientWithMeasurements_to_responseDto_without_measurements() {
        // given
        var bp = new Bloodpressure();
        bp.setId(UUID.randomUUID().toString());
        bp.setSystole(120);
        bp.setDiastole(80);
        bp.setIsDeleted(false);

        var patient = new Patient();
        patient.setId(UUID.randomUUID().toString());
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setEmail("john.doe@example.com");
        patient.setIsDeleted(false);
        patient.setBloodpressure(null);
        patient.setAddress(Address.builder()
                        .street("Main St")
                        .city("New York")
                        .state("NY")
                        .zipCode("8001")
                .build());

        // when
        var patientResponseDto = patientMapper.fromPatientWithMeasurements(patient);

        // then
        assertThat(patientResponseDto).isNotNull();
        assertThat(patientResponseDto.firstName()).isEqualTo("John");
        assertThat(patientResponseDto.bloodpressureRequestDtoList()).isNull();
    }

    @Test
    void should_map_patient_to_responseDto_without_measurements() {
        // given
        var patient = new Patient();
        patient.setId("p1");
        patient.setFirstName("Anna");
        patient.setLastName("Smith");
        patient.setEmail("anna.smith@example.com");
        patient.setGender(MALE);
        patient.setBirthDate(java.time.LocalDate.of(1985, 5, 15));
        patient.setIsDeleted(false);
        patient.setBloodpressure(null);
        var address = new Address("Baker St", "London", "Greater London", "NW1 6XE");
        patient.setAddress(address);

        // when
        var patientResponseDto = patientMapper.fromPatientWithoutMeasurements(patient);

        // then
        assertThat(patientResponseDto).isNotNull();
        assertThat(patientResponseDto.bloodpressureRequestDtoList()).isNull();
        assertThat(patientResponseDto.firstName()).isEqualTo("Anna");
    }

    @Test
    void should_map_responseDto_to_patient_with_null_fields() {
        // given
        var patientResponseDto = new PatientResponseDto(
                "id-123",
                "Max",
                "Mustermann",
                "max@example.com",
                MALE,
                null,
                false,
                null,
                null
        );

        // when
        var patient = patientMapper.toPatientFromPatientResponseDto(patientResponseDto);

        // then
        assertThat(patient).isNotNull();
        assertThat(patient.getFirstName()).isEqualTo("Max");
        assertThat(patient.getLastName()).isEqualTo("Mustermann");
        assertThat(patient.getEmail()).isEqualTo("max@example.com");
        assertThat(patient.getGender()).isEqualTo(MALE);
        assertThat(patient.getBirthDate()).isNull();

    }

    @Test
    void should_map_patient_to_responseDto_return_null() {
        //Then
        assertThat(patientMapper.fromPatientWithMeasurements(null)).isNull();
        assertThat(patientMapper.toPatientFromPatientRequestDto(null)).isNull();
        assertThat(patientMapper.toPatientFromPatientResponseDto(null)).isNull();
    }


    @Test
    void should_map_requestDto_to_patient() {
        // given
        var patientRequestDto = Instancio.of(PatientRequestDto.class)
                .set(Select.field(PatientRequestDto::firstName), "Lisa")
                .set(Select.field(PatientRequestDto::lastName), "Brown")
                .set(Select.field(PatientRequestDto::email), "lisa.brown@example.com")
                .set(Select.field(PatientRequestDto::gender), MALE)
                .set(Select.field(PatientRequestDto::birthDate), "1988-12-12")
                .create();

        // when
        var patient = patientMapper.toPatientFromPatientRequestDto(patientRequestDto);

        // then
        assertThat(patient.getFirstName()).isEqualTo("Lisa");
        assertThat(patient.getLastName()).isEqualTo("Brown");
        assertThat(patient.getEmail()).isEqualTo("lisa.brown@example.com");
    }

    @Test
    void should_map_responseDto_to_patient() {
        // given
        var patientResponseDto = new PatientResponseDto(
                "id-123",
                "Max",
                "Mustermann",
                "max@example.com",
                MALE,
                "1990-01-01",
                false,
                null,
                address
        );

        // when
        var patient = patientMapper.toPatientFromPatientResponseDto(patientResponseDto);

        // then
        assertThat(patient.getId()).isNotNull();
        assertThat(patient.getFirstName()).isEqualTo("Max");
        assertThat(patient.getLastName()).isEqualTo("Mustermann");
        assertThat(patient.getEmail()).isEqualTo("max@example.com");
        assertThat(patient.getIsDeleted()).isFalse();
        assertThat(patient.getBloodpressure()).isEmpty();
    }

    @Test
    void should_map_patient_to_patientResponseDto_with_valid_fields() {
        // given
        var patient = Patient.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@example.com")
                .gender(MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // when
        var patientResponseDto = patientMapper.fromPatientWithMeasurements(patient);

        // then
        assertThat(patientResponseDto).isNotNull();
        assertThat(patientResponseDto.firstName()).isEqualTo("John");
        assertThat(patientResponseDto.lastName()).isEqualTo("Smith");
        assertThat(patientResponseDto.email()).isEqualTo("john.smith@example.com");
        assertThat(patientResponseDto.birthDate()).isEqualTo("1990-01-01");
    }

    @Test
    void should_map_requestDto_to_patient_with_null_fields() {
        // given
        var patientRequestDto = new PatientRequestDto(
                "Max",
                "Mustermann",
                "max@example.com",
                MALE,
                null,
                address
        );

        // when
        var patient = patientMapper.toPatientFromPatientRequestDto(patientRequestDto);

        // then
        assertThat(patient).isNotNull();
        assertThat(patient.getFirstName()).isEqualTo("Max");
        assertThat(patient.getLastName()).isEqualTo("Mustermann");
        assertThat(patient.getEmail()).isEqualTo("max@example.com");
        assertThat(patient.getGender()).isEqualTo(MALE);
        assertThat(patient.getBirthDate()).isNull();
    }
}
