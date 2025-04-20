package com.spring.userservice.controller;


import com.spring.userservice.dto.*;
import com.spring.userservice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO refreshToken) {
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
    @PostMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestBody OtpVerificationRequestDTO request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

}
