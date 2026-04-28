package com.example.taskmanager.service;

import com.example.taskmanager.dto.SignupRequestDTO;
import com.example.taskmanager.dto.UserDTO;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ================= SIGNUP =================
    @Transactional
    public UserDTO signup(SignupRequestDTO signupRequest) {

        log.info("Signup request for username: {}", signupRequest.getUsername());

        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .username(signupRequest.getUsername())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        log.info("User registered successfully: {}", savedUser.getUsername());

        return new UserDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }

    // ================= FIND BY EMAIL (🔥 IMPORTANT FIX) =================
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new IllegalArgumentException("User not found");
                });
    }

    // ================= OPTIONAL (keep if needed) =================
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new IllegalArgumentException("User not found");
                });
    }

    // ================= FORGOT PASSWORD =================
    @Transactional
    public String generateResetToken(String email) {

        log.info("Generating reset token for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));

        String token = UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setResetTokenExpiry(
                Instant.now().plusSeconds(600).toEpochMilli()
        );

        userRepository.save(user);

        log.info("Reset token generated for user: {}", user.getUsername());

        return token;
    }

    // ================= RESET PASSWORD =================
    @Transactional
    public void resetPassword(String token, String newPassword) {

        log.info("Reset password attempt with token");

        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (user.getResetTokenExpiry() == null ||
                user.getResetTokenExpiry() < System.currentTimeMillis()) {
            throw new IllegalArgumentException("Token expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);

        log.info("Password reset successful for user: {}", user.getUsername());
    }
}