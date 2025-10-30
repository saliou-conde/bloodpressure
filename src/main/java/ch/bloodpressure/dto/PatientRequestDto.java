package ch.bloodpressure.dto;

import ch.bloodpressure.domain.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PatientRequestDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Email @NotBlank String email,
        @NotNull Gender gender,
        @NotBlank String birthDate, // ISO format: YYYY-MM-DD
        @NotNull AddressDto address
) {
}
