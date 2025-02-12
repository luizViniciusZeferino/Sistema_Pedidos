package org.example.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "tx+57DZAfo6NHkGROiH5KNgv10ziU2A2uimQoU0X5Ps="; // Precisa ter 32 caracteres
    private static final long EXPIRATION_TIME = 86400000; // 1 dia

    private final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String email = extractEmail(token);
            return email.equals(userDetails.getUsername());
        } catch (Exception e) {
            return false;
        }
    }
}
