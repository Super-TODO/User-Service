package com.spring.userservice.service;

import com.spring.userservice.Repository.JwtTokenRepository;
import com.spring.userservice.Repository.OtpRepository;
import com.spring.userservice.Repository.UserRepository;
import com.spring.userservice.dto.*;
import com.spring.userservice.entity.JwtToken;
import com.spring.userservice.entity.Otp;
import com.spring.userservice.entity.User;
import com.spring.userservice.utils.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtTokenRepository jwtTokenRepository;
    private final OtpRepository otpRepository;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail()) || userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("User already exists with this email or username");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(false)
                .build();

        userRepository.save(user);

        generateAndSaveOtpForUser(user);
        return AuthResponseDTO.builder()
                .accessToken(null)
                .refreshToken(null)
                .build();
    }


    private void generateAndSaveOtpForUser(User user) {
        String code = String.format("%06d",new Random().nextInt(999999));
        LocalDateTime otpExpirationTime = LocalDateTime.now().plusMinutes(5);

        // Create the OTP entity
        Otp otp= Otp.builder()
                .otpCode(code)
                .expiredAt(otpExpirationTime)
                .user(user)
                .build();

        // Save the OTP to the database
        otpRepository.save(otp);
        System.out.println("Generated OTP for " + user.getEmail() + ": " + code);
    }
    public String verifyOtp(OtpVerificationRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Otp otp = otpRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("No OTP found for this user"));

        if (otp.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP expired");
        }

        if (!otp.getOtpCode().equals(request.getOtpCode())) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        user.setEnabled(true);
        userRepository.save(user);

        otpRepository.delete(otp);

        return "Account verified successfully!";
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
        String refreshToken = jwtService.generateRefreshToken(user);
        Date accessTokenEXP = jwtService.extractExpiration(accessToken);
        Date refreshTokenEXP = jwtService.extractExpiration(refreshToken);

        JwtToken jwtToken = JwtToken.builder()
                .token(accessToken)
                .tokenType(TokenType.ACCESS)
                .isExpired(false)
                .isRevoked(false)
                .expiredAt(accessTokenEXP)
                .user(user)
                .build();

        JwtToken refresh_Token = JwtToken.builder()
                .token(refreshToken)
                .tokenType(TokenType.REFRESH)
                .isExpired(false)
                .isRevoked(false)
                .expiredAt(refreshTokenEXP)
                .user(user)
                .build();

        jwtTokenRepository.save(jwtToken);
        jwtTokenRepository.save(refresh_Token);

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO request){
        String refreshToken = request.getRefreshToken();
        String email = jwtService.extractUsername(refreshToken);

        // Check if the user exists
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No user found with this email"));

        JwtToken storedToken = jwtTokenRepository.findByToken(refreshToken);

        // Check if the refresh token is valid
        if (storedToken == null || storedToken.isExpired() || storedToken.isRevoked()) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // generate new access token
        String newAccessToken = jwtService.generateToken(user);
        Date newAccessEXP = jwtService.extractExpiration(newAccessToken);

        JwtToken newAccess_Token= JwtToken.builder()
                .token(newAccessToken)
                .tokenType(TokenType.ACCESS)
                .isExpired(false)
                .isRevoked(false)
                .expiredAt(newAccessEXP)
                .user(user)
                .build();
        jwtTokenRepository.save(newAccess_Token);

        return AuthResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public UserProfileDTO getUserProfile() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return UserProfileDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .build();
    }

    // Method to revoke all tokens for a user
    private void revokeAllUserTokens(User user) {
        var validUserTokens = jwtTokenRepository.findAllValidTokenByUser(user.getId());

        if (validUserTokens.isEmpty())
            return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        jwtTokenRepository.saveAll(validUserTokens);
    }

   public void logout(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No user found with this email"));
        revokeAllUserTokens(user);
    }
}
