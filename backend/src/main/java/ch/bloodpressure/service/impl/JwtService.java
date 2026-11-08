package ch.bloodpressure.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration}")
    private long expiration;

    public String generateToken(UserDetails userDetails) {

        var issuedAt = Instant.now();
        var expirationDate = issuedAt.plusMillis(expiration);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expirationDate))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(
            String token
    ) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {

        final var username = extractUsername(token);

        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> resolver
    ) {

        final var claims = extractAllClaims(token);

        return resolver.apply(claims);
    }

    private boolean isTokenExpired(
            String token
    ) {
        return extractExpiration(token)
                .toInstant()
                .isBefore(Instant.now());
    }

    private Date extractExpiration(
            String token
    ) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(
            String token
    ) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
