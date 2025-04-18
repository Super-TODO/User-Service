package com.spring.userservice.Repository;

import com.spring.userservice.entity.JwtToken;
import com.spring.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
    JwtToken findByToken(String token);
    List<JwtToken> findAllByUser(User user);

}
