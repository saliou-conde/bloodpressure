package ch.bloodpressure.dto;

import ch.bloodpressure.domain.Gender;

import java.util.List;

public record PatientResponseDto(
        String id,
        String firstName,
        String lastName,
        String email,
        Gender gender,
        String birthDate, // ISO format: YYYY-MM-DD
        Boolean isDeleted,
        List<BloodpressureRequestDto> bloodpressureRequestDtoList,
        AddressDto address
) {
}
