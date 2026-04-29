package com.example.taskmanager.service;

import com.example.taskmanager.dto.SignupRequestDTO;
import com.example.taskmanager.dto.UserDTO;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username already exists"
            );
        }

        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email already exists"
            );
        }

        User user = User.builder()
                .username(signupRequest.getUsername())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        log.info("User registered successfully: {}", savedUser.getEmail());

        return new UserDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }

    // ================= FIND BY EMAIL =================
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "User not found"
                    );
                });
    }

    // ================= FIND BY USERNAME =================
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "User not found"
                    );
                });
    }

    // ================= FORGOT PASSWORD =================
    @Transactional
    public String generateResetToken(String email) {

        log.info("Generating reset token for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Email not found"
                ));

        String token = UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setResetTokenExpiry(
                Instant.now().plusSeconds(600).toEpochMilli() // 10 minutes
        );

        userRepository.save(user);

        log.info("Reset token generated for user: {}", user.getEmail());

        return token;
    }

    // ================= RESET PASSWORD =================
    @Transactional
    public void resetPassword(String token, String newPassword) {

        log.info("Reset password attempt");

        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid or expired token"
                ));

        // ✅ Expiry validation
        Long expiry = user.getResetTokenExpiry();
        if (expiry == null || expiry < System.currentTimeMillis()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Token expired"
            );
        }

        // ✅ Update password
        user.setPassword(passwordEncoder.encode(newPassword));

        // ✅ Clear token
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);

        log.info("Password reset successful for user: {}", user.getEmail());
    }
}