package com.spring.userservice.entity;


import com.spring.userservice.utils.TokenType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

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

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;
    @CreationTimestamp
    private Date createdAt;
    private Date expiredAt;
    boolean isExpired= false;
    boolean isRevoked= false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
