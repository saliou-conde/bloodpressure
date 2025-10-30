package ch.bloodpressure.service.impl;

import ch.bloodpressure.domain.Bloodpressure;
import ch.bloodpressure.domain.Patient;
import ch.bloodpressure.dto.AddressDto;
import ch.bloodpressure.dto.BloodpressureRequestDto;
import ch.bloodpressure.dto.BloodpressureResponseDto;
import ch.bloodpressure.dto.PatientResponseDto;
import ch.bloodpressure.dto.mapper.BloodpressureMapper;
import ch.bloodpressure.dto.mapper.PatientMapper;
import ch.bloodpressure.exception.BloodpressureNotFoundException;
import ch.bloodpressure.repository.BloodpressureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ch.bloodpressure.domain.Gender.MALE;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class BloodpressureServiceImplTest {

    private static final String EXISTING_ID = "1";
    private static final String NOT_FOUND_ID = "not-found";

    @InjectMocks
    private BloodpressureServiceImpl service;

    @Mock
    private PatientServiceImpl patientService;

    @Mock
    private BloodpressureRepository repository;

    @Mock
    private BloodpressureMapper mapper;
    private final AddressDto address = new AddressDto("Main St", "City", "State", "12345");

    @Mock
    private PatientMapper patientMapper;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Nested
    @DisplayName("Get Bloodpressure Tests")
    class GetBloodpressureById {

        @Test
        void getBloodpressure_shouldReturnResponseDtoIfFound() {
            // Given
            var bloodpressure = createBloodpressure();
            var responseDto = createResponseDto(null,120, 80, 70, 95);

            when(repository.findByIdAndIsDeletedFalse(EXISTING_ID))
                    .thenReturn(Optional.of(bloodpressure));
            when(mapper.fromBloodpressure(bloodpressure))
                    .thenReturn(responseDto);

            // When
            var result = service.getBloodpressure(EXISTING_ID);

            // Then
            assertThat(result).isEqualTo(responseDto);
        }

        @Test
        void getBloodpressure_shouldThrowExceptionIfNotFound() {
            // Given
            when(repository.findByIdAndIsDeletedFalse(NOT_FOUND_ID))
                    .thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> service.getBloodpressure(NOT_FOUND_ID))
                    .isInstanceOf(BloodpressureNotFoundException.class)
                    .hasMessageContaining("Cannot find bloodpressure");

            // Verify
            verify(repository).findByIdAndIsDeletedFalse(NOT_FOUND_ID);
            verify(mapper, never()).fromBloodpressure(any(Bloodpressure.class));
        }

    }

    @Nested
    @DisplayName("Add Bloodpressure Tests")
    class AddBloodpressure {

        @Test
        void addBloodpressure_shouldSaveAndReturnResponseDto() {
            // Given
            var requestDto = new BloodpressureRequestDto(null, 120, 80, 60, 93);
            var responseDto = createResponseDto(null,120, 80, 60, 95);
            var bloodpressure = createBloodpressure();

            var patientResponseDto = new PatientResponseDto(
                    "patient-id",
                    "John",
                    "Doe",
                    "john.doe@gmail.com",
                    MALE,
                    "1990-01-01",
                    false,
                    List.of(requestDto),
                    address
            );
            var patient = Patient.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("")
                    .build();

            when(mapper.toBloodpressure(requestDto))
                    .thenReturn(bloodpressure);
            when(repository.save(bloodpressure))
                    .thenReturn(bloodpressure);
            when(mapper.fromBloodpressure(bloodpressure))
                    .thenReturn(responseDto);
            when(patientService.getPatient(requestDto.patientId()))
                    .thenReturn(patientResponseDto);
            when(patientMapper.toPatientFromPatientResponseDto(patientResponseDto))
                    .thenReturn(patient);

            // When
            var result = service.addBloodpressure(requestDto);

            // Then
            assertThat(result)
                    .isNotNull()
                    .extracting(
                            BloodpressureResponseDto::systole,
                            BloodpressureResponseDto::diastole,
                            BloodpressureResponseDto::heartRate,
                            BloodpressureResponseDto::isDeleted)
                    .containsExactly(120, 80, 60, false);

            // Verify
            verify(repository).save(bloodpressure);
        }

    }

    @Nested
    @DisplayName("Update Bloodpressure Tests")
    class UpdateBloodpressureById {

        @Test
        void updateBloodpressure_shouldUpdateExistingBloodpressure() {
            // Given
            var requestDto = new BloodpressureRequestDto(EXISTING_ID, 130, 85, 75, 95);
            var existing = createBloodpressure();

            when(repository.findByIdAndIsDeletedFalse(EXISTING_ID))
                    .thenReturn(Optional.of(existing));
            when(repository.save(existing))
                    .thenReturn(existing);

            // When
            service.updateBloodpressure(requestDto, EXISTING_ID);

            // Then
            assertThat(existing)
                    .extracting(
                            Bloodpressure::getSystole,
                            Bloodpressure::getDiastole,
                            Bloodpressure::getHeartRate,
                            Bloodpressure::getMap)
                    .containsExactly(130, 85, 75, 95);

            // Verify
            verify(repository).findByIdAndIsDeletedFalse(EXISTING_ID);
            verify(repository).save(existing);
        }

        @Test
        void updateBloodpressure_shouldNotUpdateBloodpressure_whenInvalidDataGiven() {
            // Given
            var requestDto = new BloodpressureRequestDto(EXISTING_ID, null, null, null, null);
            var existing = createBloodpressure();

            when(repository.findByIdAndIsDeletedFalse(EXISTING_ID))
                    .thenReturn(Optional.of(existing));
            when(repository.save(existing)).thenReturn(existing);

            // When
            service.updateBloodpressure(requestDto, EXISTING_ID);

            // Then
            assertThat(existing)
                    .extracting(
                            Bloodpressure::getSystole,
                            Bloodpressure::getDiastole,
                            Bloodpressure::getHeartRate,
                            Bloodpressure::getMap)
                    .containsExactly(120, 80, 70, 93);

            // Verify
            verify(repository).findByIdAndIsDeletedFalse(EXISTING_ID);
            verify(repository).save(existing);
        }

        @Test
        void updateBloodpressure_shouldThrowException_whenBloodpressureNotFound() {
            // Given
            var requestDto = new BloodpressureRequestDto(NOT_FOUND_ID, 130, 85, 75, 100);
            when(repository.findByIdAndIsDeletedFalse(NOT_FOUND_ID))
                    .thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> service.updateBloodpressure(requestDto, NOT_FOUND_ID))
                    .isInstanceOf(BloodpressureNotFoundException.class)
                    .hasMessageContaining("Cannot update bloodpressure");

            // Verify
            verify(repository).findByIdAndIsDeletedFalse(NOT_FOUND_ID);
            verify(repository, never()).save(any(Bloodpressure.class));
        }

    }

    @Nested
    @DisplayName("Get All Bloodpressure Tests")
    class GetBloodpressureMeasurements {

        @Test
        void getBloodpressureMeasurements_shouldReturnListOfBloodpressureMeasurements_whenListOfBloodpressureIsNotEmpty() {
            // Given
            var bp1 = createBloodpressure();
            var bp2 = Bloodpressure.builder()
                    .systole(125)
                    .diastole(70)
                    .heartRate(70)
                    .build();

            var dto1 = createResponseDto(bp1.getId(),120, 80, 70, 100);
            var dto2 = createResponseDto(bp2.getId(),125, 70, 70, 95);

            when(repository.findAllByIsDeletedFalse())
                    .thenReturn(List.of(bp1, bp2));
            when(mapper.fromBloodpressure(bp1))
                    .thenReturn(dto1);
            when(mapper.fromBloodpressure(bp2))
                    .thenReturn(dto2);

            // When
            var result = service.getBloodpressure();

            // Then
            assertThat(result)
                    .containsExactlyInAnyOrder(dto1, dto2);

            // Verify
            verify(repository).findAllByIsDeletedFalse();
        }

        @Test
        void getBloodpressureMeasurementsByPatientId_shouldReturnMeasurements_whenListOfBloodpressureIsNotEmpty() {
            // Given
            var bp1 = createBloodpressure();
            var bp2 = Bloodpressure.builder()
                    .systole(125)
                    .diastole(70)
                    .heartRate(70)
                    .build();

            var dto1 = createResponseDto(bp1.getId(),120, 80, 70,100);
            var dto2 = createResponseDto(bp2.getId(),125, 70, 70, 95);

            when(repository.findBloodpressureByPatientId(EXISTING_ID))
                    .thenReturn(List.of(bp1, bp2));
            when(mapper.fromBloodpressure(bp1))
                    .thenReturn(dto1);
            when(mapper.fromBloodpressure(bp2))
                    .thenReturn(dto2);

            // When
            var result = service.getBloodpressureMeasurementsByPatientId(EXISTING_ID);

            // Then
            assertThat(result)
                    .containsExactlyInAnyOrder(dto1, dto2);

            // Verify
            verify(repository).findBloodpressureByPatientId(EXISTING_ID);
        }

        @Test
        void getBloodpressureMeasurementsByPatientId_shouldReturnEmptyList_whenListOfBloodpressureIsEmpty() {
            // Given
            when(repository.findBloodpressureByPatientId(NOT_FOUND_ID))
                    .thenReturn(List.of());

            // When
            var result = service.getBloodpressureMeasurementsByPatientId(NOT_FOUND_ID);

            // Then
            assertThat(result).isEmpty();

            // Verify
            verify(repository).findBloodpressureByPatientId(NOT_FOUND_ID);
        }

    }

    @Nested
    @DisplayName("Delete Bloodpressure Tests")
    class DeleteBloodpressure {

        @Test
        void deleteBloodpressure_shouldMarkAsDeleted_whenBloodpressureExists() {
            // Given
            var bloodpressure = createBloodpressure();
            when(repository.findByIdAndIsDeletedFalse(EXISTING_ID))
                    .thenReturn(Optional.of(bloodpressure));

            // When
            service.deleteBloodpressure(EXISTING_ID);

            // Then
            assertThat(bloodpressure.getIsDeleted()).isTrue();

            // Verify
            verify(repository).save(bloodpressure);
        }

        @Test
        void deleteBloodpressure_shouldThrowException_whenBloodpressureNotFound() {
            // Given
            when(repository.findByIdAndIsDeletedFalse(NOT_FOUND_ID))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> service.deleteBloodpressure(NOT_FOUND_ID))
                    .isInstanceOf(BloodpressureNotFoundException.class)
                    .hasMessageContaining("Cannot delete bloodpressure");

            // Verify
            verify(repository).findByIdAndIsDeletedFalse(NOT_FOUND_ID);
            verify(repository, never()).save(any(Bloodpressure.class));
        }

    }

    private Bloodpressure createBloodpressure() {
        return Bloodpressure.builder()
                .systole(120)
                .diastole(80)
                .heartRate(70)
                .map(93)
                .build();
    }

    private BloodpressureResponseDto createResponseDto(String id, int systole, int diastole, int heartRate, int map) {
        return new BloodpressureResponseDto(
                id,
                systole,
                diastole,
                heartRate,
                map,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                false
        );
    }
}
