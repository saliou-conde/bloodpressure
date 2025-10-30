package ch.bloodpressure.controller;

import ch.bloodpressure.dto.BloodpressureRequestDto;
import ch.bloodpressure.dto.BloodpressureResponseDto;
import ch.bloodpressure.service.BloodpressureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.ACCEPTED;

class BloodpressureControllerTest {

    @Mock
    private BloodpressureService service;

    @InjectMocks
    private BloodpressureController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addBloodpressure_shouldInsertBloodpressure_whenValidDataGiven() {
        //Given
        var requestDto = createRequestDto(null);
        var responseDto = createResponseDto(null);

        // Mocking
        when(service.addBloodpressure(requestDto)).thenReturn(responseDto);

        //When
        ResponseEntity<BloodpressureResponseDto> result = controller.addBloodpressure(requestDto);

        //Then
        assertThat(result.getBody()).isEqualTo(responseDto);
        assertThat(result.getStatusCode().value()).isEqualTo(200);

        //Verify
        verify(service, times(1)).addBloodpressure(requestDto);
        verifyNoMoreInteractions(service);
    }

    @Test
    void getBloodpressure_shouldReturnBloodpressureList_whenBloodpressureExist() {
        //Given
        var responseList = List.of(createResponseDto(null));

        // Mocking
        when(service.getBloodpressure()).thenReturn(responseList);

        //When
        var result = controller.getBloodpressureMeasurements();

        //Then
        assertThat(result.getBody()).isEqualTo(responseList);
        assertThat(result.getStatusCode().value()).isEqualTo(200);

        //Verify
        verify(service, times(1)).getBloodpressure();
        verifyNoMoreInteractions(service);
    }

    @Test
    void getBloodpressureMeasurementsByPatientId_shouldReturnBloodpressureList_whenBloodpressureExist() {
        //Given
        String patientId = "1";
        var responseList = List.of(createResponseDto(patientId));

        // Mocking
        when(service.getBloodpressureMeasurementsByPatientId(patientId)).thenReturn(responseList);

        //When
        var result = controller.getBloodpressureMeasurementsByPatientId(patientId);

        //Then
        assertThat(result.getBody()).isEqualTo(responseList);
        assertThat(result.getStatusCode().value()).isEqualTo(200);

        //Verify
        verify(service, times(1)).getBloodpressureMeasurementsByPatientId(patientId);
        verifyNoMoreInteractions(service);
    }

    @Test
    void getBloodpressureById_shouldFindBloodpressure_whenBloodpressureExists() {
        //Given
        String id = "1";
        var responseDto = createResponseDto(null);

        // Mocking
        when(service.getBloodpressure(id)).thenReturn(responseDto);

        //When
        var result = controller.getBloodpressureById(id);

        //Then
        assertThat(result.getBody()).isEqualTo(responseDto);
        assertThat(result.getStatusCode().value()).isEqualTo(200);

        //Verify
        verify(service, times(1)).getBloodpressure(id);
        verifyNoMoreInteractions(service);
    }

    @Test
    void updateBloodpressure_shouldModifyBloodpressure_whenValidDataGiven() {
        //Given
        String id = "1";
        var requestDto = createRequestDto(id);

        //When
        var result = controller.updateBloodpressure(id, requestDto);

        //Then
        assertThat(result.getStatusCode().value()).isEqualTo(202);

        //Verify
        verify(service, times(1)).updateBloodpressure(requestDto, id);
        verifyNoMoreInteractions(service);
    }

    @Test
    void deleteBloodpressure_shouldSoftDelete_whenBloodpressurePresent() {
        //Given
        String id = "1";

        //When
        var result = controller.deleteBloodpressure(id);

        //Then
        assertThat(result.getStatusCode()).isEqualTo(ACCEPTED);

        //Verify
        verify(service).deleteBloodpressure(id);
        verifyNoMoreInteractions(service);
    }

    private BloodpressureRequestDto createRequestDto(String id) {
        var systole = (int) (90 + Math.random() * 60);
        var diastole = (int) (60 + Math.random() * 40);
        var heartRate = (int) (60 + Math.random() * 40);
        var map = (systole + 2*diastole)/3;
        return new BloodpressureRequestDto(id, systole, diastole, heartRate, map);
    }

    private BloodpressureResponseDto createResponseDto(String id) {
        return new BloodpressureResponseDto(id,120, 80, 70, 102,null, null, null);
    }

}