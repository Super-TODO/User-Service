package com.spring.userservice.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final Date expirationDate= new Date(System.currentTimeMillis() + 1000 * 60 * 60); // 1 hour expiration time
    @Value("${jwt.secret}")
    private String secret_key;



    // This method generates a JWT token for the given user details
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) // token issued at current time
                .setExpiration(expirationDate) // token expiration time
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // signing algorithm
                .compact();   // token compacting to string
    }

    // extracting the username from the token(email)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // This method checks if the token is valid for the given user details
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // This method checks if the token is expired
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // This method extracts the expiration date from the token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // This method extracts a specific claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // This method extracts all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // This method gets the signing key for the JWT token
    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret_key));

    }

}
