package com.spring.userservice.controller;

import com.spring.userservice.Repository.JwtTokenRepository;
import com.spring.userservice.Repository.UserRepository;
import com.spring.userservice.dto.*;
import com.spring.userservice.entity.JwtToken;
import com.spring.userservice.entity.User;
import com.spring.userservice.service.AuthService;
import com.spring.userservice.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenRepository jwtTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok("Logout successful. Token revoked.");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        return ResponseEntity.ok(authService.getUserProfile());
    }

    @PostMapping("/send-otp")
    public ResponseEntity<Void> sendOtp(@RequestBody Map<String, String> body) {
        authService.sendOtpToEmail(body.get("email"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<Void> resendOtp(@RequestBody Map<String, String> body) {
        authService.resendOtp(body.get("email"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyAccount(@Valid @RequestBody OtpVerificationRequestDTO request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Void> validateToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        JwtToken jwtToken = jwtTokenRepository.findByToken(token);
        if (jwtToken == null || jwtToken.isExpired() || jwtToken.isRevoked()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!jwtService.isTokenValid(token, user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().build();
    }
}
