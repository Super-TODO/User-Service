package com.spring.userservice.Repository;

import com.spring.userservice.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Otp findByUserAndOtp(Long user, String otp);

}
