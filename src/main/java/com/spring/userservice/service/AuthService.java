package com.spring.userservice.service;

import com.spring.userservice.Repository.JwtTokenRepository;
import com.spring.userservice.Repository.UserRepository;
import com.spring.userservice.dto.AuthResponseDTO;
import com.spring.userservice.dto.LoginRequestDTO;
import com.spring.userservice.dto.RegisterRequestDTO;
import com.spring.userservice.entity.JwtToken;
import com.spring.userservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtTokenRepository jwtTokenRepository;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail()) || userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("User already exists with this email or username");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .build();

        userRepository.save(user);

        return getAuthResponseDTO(user);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = request.getEmail().contains("@")
                ? userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("No user found with this email"))
                : userRepository.findByUsername(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("No user found with this username"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new BadCredentialsException("Incorrect password");

        return getAuthResponseDTO(user);
    }

    private AuthResponseDTO getAuthResponseDTO(User user) {
        String accessToken = jwtService.generateToken(user);
        Date expiresAt = jwtService.extractExpiration(accessToken);

        JwtToken jwtToken = JwtToken.builder()
                .token(accessToken)
                .tokenType("Bearer")
                .isExpired(false)
                .isRevoked(false)
                .expiredAt(expiresAt)
                .user(user)
                .build();

        jwtTokenRepository.save(jwtToken);

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .build();
    }
}
