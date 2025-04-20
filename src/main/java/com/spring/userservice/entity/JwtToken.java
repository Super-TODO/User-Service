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
public class JwtToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String token;
    private String tokenType;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    boolean isExpired= false;
    boolean isRevoked= false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
