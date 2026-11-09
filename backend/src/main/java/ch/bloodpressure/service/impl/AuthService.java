package ch.bloodpressure.service.impl;

import ch.bloodpressure.domain.BPUser;
import ch.bloodpressure.domain.Role;
import ch.bloodpressure.dto.LoginRequest;
import ch.bloodpressure.dto.LoginResponse;
import ch.bloodpressure.dto.RegisterRequest;
import ch.bloodpressure.dto.ResetPasswordRequest;
import ch.bloodpressure.exception.BPUserAlreadyExistException;
import ch.bloodpressure.exception.BPUserNotFoundException;
import ch.bloodpressure.exception.AccountDeletionNotAllowedException;
import ch.bloodpressure.repository.BPUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String USER_NOT_FOUND_WITH_USERNAME = "User not found with username: ";
    private final AuthenticationManager authenticationManager;
    private final BPUserRepository bpUserRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public LoginResponse authenticate(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );


        var user = findByUsername(request.username())
                .orElseThrow(() -> new BPUserNotFoundException(USER_NOT_FOUND_WITH_USERNAME + request.username()));

        var userDetails = AuthServiceUtils.getUserDetails(user);

        return new LoginResponse(jwtService.generateToken(userDetails));
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {

        var user = findByUsername(request.username());
        if(user.isPresent()) {
            throw new BPUserAlreadyExistException("User already exists with username: " + request.username());
        }

        var newUser = new BPUser();
        newUser.setEmail(request.email());
        newUser.setUsername(request.username());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(Role.NURSE);
        var savedUser = bpUserRepository.save(newUser);

        UserDetails userDetails = AuthServiceUtils.getUserDetails(savedUser);
        return new LoginResponse(jwtService.generateToken(userDetails));
    }

    @Transactional
    public void deleteUser(String username) {
        var user = findByUsername(username)
                .orElseThrow(() -> new BPUserNotFoundException(USER_NOT_FOUND_WITH_USERNAME + username));
        if(AuthServiceUtils.isAdminUser(user)) {
            throw new AccountDeletionNotAllowedException("Cannot delete user with role: " + user.getRole());
        }
        bpUserRepository.delete(user);
    }

    @Transactional
    public LoginResponse resetPassword(ResetPasswordRequest request) {
        var username = request.username();
        var user = findByUsername(username)
                .orElseThrow(() -> new BPUserNotFoundException(USER_NOT_FOUND_WITH_USERNAME + username));
        if(!user.getEmail().equals(request.email())) {
            throw new BPUserNotFoundException("Email does not match for user: " + username);
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        bpUserRepository.save(user);
        return new LoginResponse(jwtService.generateToken(AuthServiceUtils.getUserDetails(user)));
    }

    private Optional<BPUser> findByUsername(String username) {
        return bpUserRepository
                .findByUsername(username);
    }

    /**
     * Utility class for authentication-related operations.
     */
    private static final  class AuthServiceUtils {

        public static boolean isAdminUser(BPUser user) {
            return user.getRole() == Role.ADMIN || user.getRole() == Role.SUPER_ADMIN;
        }

        public static UserDetails getUserDetails(BPUser user) {
            return org.springframework.security.core.userdetails.User
                    .builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole().name())
                    .build();
        }
    }

}
