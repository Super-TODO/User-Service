package com.spring.userservice.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpVerificationRequestDTO {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")   
    private String email;
    @NotBlank(message = "OTP code is required")
    private String otpCode;
}
