package com.example.taskmanager.security;

import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    // You can also inject this as a bean if preferred
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        try {
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

            String email = oauthUser.getAttribute("email");
            String name = oauthUser.getAttribute("name");

            // ✅ Safety check
            if (email == null || email.isEmpty()) {
                response.sendRedirect("https://taskmanager-2-ykxw.onrender.com/?error=email_missing");
                return;
            }

            Optional<User> userOptional = userRepository.findByEmail(email);

            User user;

            // 🔥 AUTO SIGNUP
            if (userOptional.isEmpty()) {

                user = User.builder()
                        .username(generateUsername(name))
                        .email(email)
                        .password(passwordEncoder.encode("GOOGLE_USER"))
                        .build();

                userRepository.save(user);

            } else {
                user = userOptional.get();
            }

            // 🔥 IMPORTANT: Use EMAIL (consistent with login)
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    null
            );

            String token = jwtUtils.generateToken(auth);

            // ✅ SUCCESS REDIRECT
            response.sendRedirect(
                    "https://taskmanager-2-ykxw.onrender.com/oauth-success?token=" + token
            );

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 Helps debugging in logs

            // ❌ FAIL SAFE REDIRECT
            response.sendRedirect(
                    "https://taskmanager-2-ykxw.onrender.com/?error=oauth_failed"
            );
        }
    }

    private String generateUsername(String name) {
        if (name == null || name.isEmpty()) return "user";

        return name.toLowerCase()
                .replaceAll(" ", "")
                .replaceAll("[^a-z0-9]", "");
    }
}