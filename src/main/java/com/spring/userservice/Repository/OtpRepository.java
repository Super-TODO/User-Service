package com.spring.userservice.Repository;

import com.spring.userservice.entity.Otp;
import com.spring.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Otp findByOtpCode(String otpCode);

    Optional<Otp>  findByUser(User user);
}
