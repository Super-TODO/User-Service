package com.spring.userservice.Repository;

import com.spring.userservice.entity.JwtToken;
import com.spring.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
    JwtToken findByToken(String token);

    @Query("""
    SELECT t FROM JwtToken t
    WHERE t.user.id = :userId AND (t.isExpired = false OR t.isRevoked = false)
""")
    List<JwtToken> findAllValidTokenByUser(Long userId);
}
