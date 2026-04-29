package com.example.taskmanager.controller;

import com.example.taskmanager.dto.AuthResponseDTO;
import com.example.taskmanager.dto.LoginRequestDTO;
import com.example.taskmanager.dto.SignupRequestDTO;
import com.example.taskmanager.dto.UserDTO;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.security.JwtUtils;
import com.example.taskmanager.service.EmailService;
import com.example.taskmanager.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    // ================= SIGNUP =================
    @PostMapping("/signup")
    @Operation(summary = "Register a new user")
    public ResponseEntity<UserDTO> signup(@Valid @RequestBody SignupRequestDTO signupRequest) {

        log.info("Signup request for username: {}", signupRequest.getUsername());

        UserDTO userDTO = userService.signup(signupRequest);
        return ResponseEntity.ok(userDTO);
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get JWT token")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {

        try {
            log.info("Login attempt for email: {}", loginRequest.getEmail());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateToken(authentication);

            User user = userService.findByEmail(loginRequest.getEmail());

            AuthResponseDTO authResponse = new AuthResponseDTO(
                    jwt,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail()
            );

            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException e) {
            log.error("Invalid login attempt for email: {}", loginRequest.getEmail());
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    // ================= FORGOT PASSWORD =================
    @PostMapping("/forgot-password")
    @Operation(summary = "Send reset password email")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {

        log.info("🔥 Forgot password request for email: {}", email);

        try {
            String token = userService.generateResetToken(email);

            String resetLink =
                    "https://taskmanager-2-ykxw.onrender.com/reset/" + token;

            log.info("🔗 Generated reset link: {}", resetLink);

            // 🔥 IMPORTANT: actually call email service
            emailService.sendResetEmail(email, resetLink);

            log.info("✅ Email sent successfully to {}", email);

            return ResponseEntity.ok("Reset link sent to your email");

        } catch (Exception e) {
            log.error("❌ Email sending failed: {}", e.getMessage());

            // 🔥 VERY IMPORTANT: return error instead of fake success
            return ResponseEntity.status(500)
                    .body("Email sending failed: " + e.getMessage());
        }
    }

    // ================= RESET PASSWORD =================
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using token")
    public ResponseEntity<?> resetPassword(
            @RequestParam String token,
            @RequestParam String password) {

        log.info("Reset password request");

        userService.resetPassword(token, password);

        return ResponseEntity.ok("Password updated successfully");
    }
}