package ch.bloodpressure.dto;

import java.time.LocalDateTime;

public record BloodpressureResponseDto(
        String id,
        Integer systole,
        Integer diastole,
        Integer heartRate,
        Integer map,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean isDeleted
) {
}
