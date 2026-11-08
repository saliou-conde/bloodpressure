package ch.bloodpressure.service.impl;

import ch.bloodpressure.dto.LoginResponse;
import ch.bloodpressure.repository.BPUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BPUserService {

    private final BPUserRepository bpUserRepository;

    public LoginResponse login(String username, String password) {
        // Implement your login logic here
        // For example, you can check the username and password against the database
        // If valid, generate a token and return it in the LoginResponse

        // This is just a placeholder implementation
        if (username.equals("admin") && password.equals("password")) {
            String token = "dummy-token"; // Replace with actual token generation logic
            return new LoginResponse(token);
        } else {
            throw new RuntimeException("Invalid username or password");
        }
    }

}
