package com.example.taskmanager.security;

import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

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

        // 🔥 FIX: use EMAIL (same as normal login)
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                null
        );

        String token = jwtUtils.generateToken(auth);

        // 🔥 Redirect with token
        response.sendRedirect("http://localhost:3000/oauth-success?token=" + token);
    }

    private String generateUsername(String name) {
        if (name == null) return "user";

        return name.toLowerCase()
                .replaceAll(" ", "")
                .replaceAll("[^a-z0-9]", "");
    }
}