package com.spring.userservice.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String otpCode;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
