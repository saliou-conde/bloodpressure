package ch.bloodpressure.controller;

import ch.bloodpressure.dto.LoginRequest;
import ch.bloodpressure.dto.LoginResponse;
import ch.bloodpressure.dto.RegisterRequest;
import ch.bloodpressure.service.impl.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        // Implement registration logic here
        LoginResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

}
