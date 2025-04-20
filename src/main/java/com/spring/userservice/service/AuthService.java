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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtTokenRepository jwtTokenRepository;

    public AuthResponseDTO register(RegisterRequestDTO request){

        // Check if the user already exists
        if (userRepository.existsByEmail(request.getEmail()) || userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("User already exists with this email or username");
        }

        // Create a new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .build();
        // Save the user to the database
        userRepository.save(user);

        // Generate JWT token
        String accessToken = jwtService.generateToken(user);
        JwtToken jwtToken = JwtToken.builder()
                .token(accessToken)
                .tokenType("Bearer")
                .isExpired(false)
                .isRevoked(false)
                .user(user)
                .build();
        // Save the JWT token to the database
        jwtTokenRepository.save(jwtToken);

        // Return the response with the access token
        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .build();
    }


    public AuthResponseDTO login(LoginRequestDTO request){

        // Fetch user by email or username
        User user= request.getEmail().contains("@")?
                userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new IllegalArgumentException("No user found with this email")):
                userRepository.findByUsername(request.getEmail())
                .orElseThrow(()->new IllegalArgumentException("No user found with this username"));

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(),user.getPassword()))
            throw new BadCredentialsException("Incorrect password");

        // Generate JWT token
        String accessToken = jwtService.generateToken(user);
        JwtToken jwtToken = JwtToken.builder()
                .token(accessToken)
                .tokenType("Bearer")
                .isExpired(false)
                .isRevoked(false)
                .user(user)
                .build();

        // Save the JWT token to the database
        jwtTokenRepository.save(jwtToken);

        // Return the response with the access token
        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .build();
    }

}
