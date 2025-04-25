package com.spring.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Hanin's TODO App - Verify Your Email");
        message.setText(
                """
                Hello,

                Thank you for registering with Hanin's TODO App!
                
                Your One-Time Password (OTP) for email verification is: %s

                This OTP is valid for only 5 minutes.

                If you did not request this, please ignore this email.

                Best regards,
                Hanin's TODO App Team
                """.formatted(otpCode)
        );

        mailSender.send(message);
    }
}
