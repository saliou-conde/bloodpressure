package ch.bloodpressure.service.impl;

import ch.bloodpressure.domain.Address;
import ch.bloodpressure.domain.Bloodpressure;
import ch.bloodpressure.domain.Gender;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static ch.bloodpressure.domain.Gender.MALE;
import static java.lang.Boolean.FALSE;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class PatientServiceImplTest {

    @InjectMocks
    private PatientServiceImpl patientService;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private BloodpressureRepository bloodpressureRepository;

    @Mock
    private PatientMapper patientMapper;

    private final AddressDto address = new AddressDto("Main St", "City", "State", "12345");
    private final Address addressEntity = Address.builder()
            .street("Main St")
            .city("City")
            .state("State")
            .zipCode("12345")
            .build();

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void getPatients_shouldReturnPatientList() {
        // Given
        String id1 = "1";
        String id2 = "2";

        Patient patient1 = buildPatient("John", "Doe", "john.doe@gmail.com");
        Patient patient2 = buildPatient("Jane", "Smith", "jane.doe@gmail.com");
        List<Patient> patientList = List.of(patient1, patient2);

        PatientResponseDto response1 = buildPatientResponseDto(id1, "John", "Doe",
                "john.doe@gmail.com",
                List.of(
                        buildBloodpressureRequestDto(id1, 120, 80, 70, 93),
                        buildBloodpressureRequestDto(id1, 130, 85, 75, 100))
        );

        PatientResponseDto response2 = buildPatientResponseDto(
                id2, "Jane", "Smith", "jane.doe@gmail.com",
                List.of(
                        buildBloodpressureRequestDto(id2, 124, 70, 70, 93),
                        buildBloodpressureRequestDto(id2, 131, 85, 75, 100))
        );

        when(patientMapper.fromPatientWithoutMeasurements(patient1)).thenReturn(response1);
        when(patientMapper.fromPatientWithoutMeasurements(patient2)).thenReturn(response2);
        when(patientRepository.findAllPatientsOnly()).thenReturn(patientList);

        // When
        var result = patientService.getPatients();

        // Then
        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.getFirst().bloodpressureRequestDtoList()).isNotNull()
                .containsExactlyElementsOf(response1.bloodpressureRequestDtoList());
        assertThat(result.getLast().bloodpressureRequestDtoList()).isNotNull()
                .containsExactlyElementsOf(response2.bloodpressureRequestDtoList());
    }

    @Test
    void getPatients_shouldReturnEmptyList() {
        // Given
        when(patientRepository.findAllPatientsOnly()).thenReturn(List.of());

        // When & Then
        assertThatThrownBy(() -> patientService.getPatients())
                .isInstanceOf(PatientNotFoundException.class)
                .hasMessageContaining("No patients found in the database.");
    }

    @Test
    void getPatient_shouldFindPatient_whenPatientPresent() {
        // Given
        String id = "1";

        Patient patient = buildPatient("John", "Doe", "john.doe@gmail.com");
        PatientResponseDto response = buildPatientResponseDto(
                id, "John", "Doe", "john.doe@gmail.com",
                List.of(
                        buildBloodpressureRequestDto(id, 120, 80, 70, 93),
                        buildBloodpressureRequestDto(id, 130, 85, 75, 93))
        );

        when(patientRepository.findPatientByIdWithMeasurements(id)).thenReturn(Optional.of(patient));
        when(patientMapper.fromPatientWithMeasurements(patient)).thenReturn(response);
        List<Bloodpressure> measurements = List.of(
                Bloodpressure.builder().systole(120).diastole(80).heartRate(70).map(104).build(),
                Bloodpressure.builder().systole(130).diastole(85).heartRate(75).map(102).build());
        when(bloodpressureRepository.findBloodpressureByPatientId(id)).thenReturn(measurements);
        patient.setBloodpressure(measurements);

        // When
        var result = patientService.getPatient(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(result.bloodpressureRequestDtoList().getFirst().patientId());
        assertThat(result.firstName()).isEqualTo("John");
        assertThat(result.lastName()).isEqualTo("Doe");
        assertThat(result.email()).isEqualTo("john.doe@gmail.com");
        assertThat(result.bloodpressureRequestDtoList()).hasSize(2);
    }


    @Test
    void getPatient_shouldThrowExceptionIfNotFound() {
        // Given
        String id = "1";

        when(patientRepository.findPatientByIdWithMeasurements(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> patientService.getPatient(id))
                .isInstanceOf(PatientNotFoundException.class)
                .hasMessageContaining("Cannot get patient:: No patient found with the provided Id:: " + id);
    }

    @Test
    void createPatient_shouldInsertPatient_whenValidDataGiven() {
        // Given
        String id = "1";
        Patient patient = buildPatient("John", "Doe", "john.doe@gmail.com");
        PatientRequestDto requestDto = buildPatientRequestDto("John", "Doe", "john.doe@gmail.com", MALE, "1990-01-01");
        PatientResponseDto response = buildPatientResponseDto(id, "John", "Doe", "john.doe@gmail.com",
                List.of(
                        buildBloodpressureRequestDto(id, 120, 80, 70, 93),
                        buildBloodpressureRequestDto(id, 130, 85, 75, 93))
        );

        when(patientMapper.fromPatientWithoutMeasurements(patient)).thenReturn(response);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toPatientFromPatientRequestDto(requestDto)).thenReturn(patient);

        // When
        var result = patientService.createPatient(requestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("John");
        assertThat(result.lastName()).isEqualTo("Doe");
        assertThat(result.isDeleted()).isFalse();

    }

    @Test
    void createPatient_shouldReturnException_whenEmailAlreadyInUse() {
        // Given
        PatientRequestDto requestDto = buildPatientRequestDto("John", "Doe", "john.doe@gmail.com", MALE, "1990-01-01");
        String email = requestDto.email();

        when(patientRepository.existsByEmailIgnoreCase(email)).thenReturn(Boolean.TRUE);

        // When / Then
        assertThatThrownBy(() -> patientService.createPatient(requestDto))
                .isInstanceOf(EmailAlreadyExistException.class)
                .hasMessageContaining(format("Patient with email %s already exists.", email));
    }

    @Test
    void deletePatient_shouldMarkAsDeleted() {
        // Given
        String id = "1";
        Patient patient = buildPatient("John", "Doe", "john.doe@gmail.com");
        List<Bloodpressure> measurements = List.of(
                Bloodpressure.builder().systole(120).diastole(80).heartRate(70).build(),
                Bloodpressure.builder().systole(130).diastole(85).heartRate(75).build()
        );
        patient.setBloodpressure(measurements);

        when(patientRepository.findPatientByIdWithMeasurements(id)).thenReturn(Optional.of(patient));
        when(patientRepository.save(patient)).thenReturn(patient);

        // When
        patientService.deletePatient(id);

        // Then
        assertThat(patient.getIsDeleted()).isTrue();
        assertThat(patient.getBloodpressure()).allMatch(bp -> bp.getIsDeleted() != null && bp.getIsDeleted());

        //Verify
        verify(patientRepository, times(1)).findPatientByIdWithMeasurements(id);
        verify(patientRepository, times(1)).save(patient);

    }

    @Test
    void deleteBloodpressure_shouldThrowExceptionIfNotFound() {
        // Given
        String id = "not-found";

        // Mocking
        when(patientRepository.findPatientByIdWithMeasurements(id))
                .thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> patientService.deletePatient(id))
                .isInstanceOf(PatientNotFoundException.class)
                .hasMessageContaining("Cannot delete patient:: No patient found with the provided Id:: " + id);

        //Verify
        verify(patientRepository, times(1)).findPatientByIdWithMeasurements(id);
    }

    @Test
    void updatePatient_shouldUpdateExistingPatient() {
        // Given
        String id = "1";
        Patient patient = buildPatient("Janine", "Doe", "janine.doe@gmail.com");
        PatientRequestDto requestDto = buildPatientRequestDto("Jane", "Smitt", "jane.smitt@gmail.com", MALE, "1990-01-01");

        when(patientRepository.findPatientByIdWithMeasurements(id)).thenReturn(Optional.of(patient));
        when(patientRepository.save(patient)).thenReturn(patient);

        // When
        patientService.updatePatient(requestDto, id);

        // Then
        assertThat(patient.getFirstName()).isEqualTo("Jane");
        assertThat(patient.getLastName()).isEqualTo("Smitt");
        assertThat(patient.getEmail()).isEqualTo("jane.smitt@gmail.com");
        assertThat(patient.getIsDeleted()).isFalse();
    }

    @Test
    void updatePatient_shouldNotUpdateExistingPatient() {
        // Given
        String id = "1";
        Patient patient = buildPatient("Janine", "Doe", "janine.doe@gmail.com");
        PatientRequestDto requestDto = buildPatientRequestDto(null, null, null, null, null);

        when(patientRepository.findPatientByIdWithMeasurements(id)).thenReturn(Optional.of(patient));
        when(patientRepository.save(patient)).thenReturn(patient);

        // When
        patientService.updatePatient(requestDto, id);

        // Then
        assertThat(patient.getFirstName()).isEqualTo("Janine");
        assertThat(patient.getLastName()).isEqualTo("Doe");
        assertThat(patient.getEmail()).isEqualTo("janine.doe@gmail.com");
        assertThat(patient.getIsDeleted()).isFalse();

    }

    @Test
    void deleteAll_shouldMarkAllAsDeleted() {
        // When
        patientService.deleteAll();

        // Then
        verify(bloodpressureRepository, times(1)).markAllBloodpressureAsDeleted();
        verify(patientRepository, times(1)).markAllPatientsAsDeleted();
    }

    private Patient buildPatient(String firstName, String lastName, String email) {
        return Patient.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .birthDate(LocalDate.of(1990, 1, 1))
                .gender(MALE)
                .address(addressEntity)
                .build();
    }

    private PatientResponseDto buildPatientResponseDto(String id, String firstName, String lastName, String email,
                                                       List<BloodpressureRequestDto> measurements) {
        return new PatientResponseDto(id, firstName, lastName, email, MALE,"1990-01-01", FALSE, measurements, address);
    }

    private BloodpressureRequestDto buildBloodpressureRequestDto(String patientId, Integer systole, Integer diastole,
                                                                 Integer heartRate, Integer map) {
        return new BloodpressureRequestDto(patientId, systole, diastole, heartRate, map);
    }

    private PatientRequestDto buildPatientRequestDto(String firstName, String lastName, String email, Gender gender, String birthDate) {
        return new PatientRequestDto(firstName, lastName, email, gender, birthDate, address);
    }

}