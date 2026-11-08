package ch.bloodpressure.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Email is required") @Email
        String email,
        @NotBlank(message = "New password is required")
        String newPassword
) {
}
