package ch.bloodpressure.controller;

import ch.bloodpressure.dto.LoginRequest;
import ch.bloodpressure.dto.LoginResponse;
import ch.bloodpressure.dto.RegisterRequest;
import ch.bloodpressure.dto.ResetPasswordRequest;
import ch.bloodpressure.service.impl.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody @Valid RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<LoginResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        var response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-account/{username}")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.username")
    public ResponseEntity<Void> deleteAccount(@PathVariable String username) {
        authService.deleteUser(username);
        return ResponseEntity.ok().build();
    }

}
