package ch.bloodpressure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BloodpressureRequestDto(
        @NotBlank(message = "Patient ID is required") String patientId,
        @NotNull(message = "Systolic BP is required") Integer systole,
        @NotNull(message = "Diastolic BP is required") Integer diastole,
        @NotNull(message = "HeartRate is required") Integer heartRate,
        Integer map
        ) {
}
