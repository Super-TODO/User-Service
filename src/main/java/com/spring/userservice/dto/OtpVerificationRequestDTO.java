package com.spring.userservice.dto;


import lombok.Data;

@Data
public class OtpVerificationRequestDTO {
    private String email;
    private String otpCode;
}
