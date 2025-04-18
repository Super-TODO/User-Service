package com.spring.userservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank(message = "Enter your Username, please")
    private String username;

    @NotBlank(message = "Enter your Email, please")
    @Email
    @Column(unique = true,nullable = false)
    private String email;

    @NotBlank(message = "Enter your Password, please")
    @Column(nullable = false)
    private String password;
    private boolean enabled;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(insertable = false)
    private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Otp> otps;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<JwtToken> jwtTokens;
}
