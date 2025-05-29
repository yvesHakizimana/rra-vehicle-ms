package com.ne.rra_vehicle_ms.auth;


import com.ne.rra_vehicle_ms.auth.exceptions.InvalidJwtException;
import com.ne.rra_vehicle_ms.employee.entities.Employee;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@AllArgsConstructor
@Service
@Slf4j
public class JwtService {
    private final JwtConfig config;

    Jwt generateAccessToken(Employee employee){
        return generateToken(employee, config.getAccessTokenExpiration());
    }

    Jwt generateRefreshToken(Employee employee){
        return generateToken(employee, config.getRefreshTokenExpiration());
    }

    private Jwt generateToken(Employee employee, long tokenExpiration){
        var claims = Jwts.claims()
                .subject(employee.getId().toString())
                .add("email", employee.getEmail())
                .add("phoneNumber", employee.getMobile())
                .add("role", employee.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
                .build();
        return new Jwt(claims, config.getSecretKey());
    }

    Jwt parseToken(String token) {
        try {
            var claims = getClaims(token);
            return new Jwt(claims, config.getSecretKey());
        } catch (ExpiredJwtException ex) {
            log.debug("Token expired: {}", ex.getMessage()); // Debug level only
            throw new InvalidJwtException("Token expired");
        } catch (SignatureException ex) {
            log.debug("Invalid token signature: {}", ex.getMessage()); // Debug level only
            throw new InvalidJwtException("Invalid token signature");
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Invalid token: {}", ex.getMessage()); // Debug level only
            throw new InvalidJwtException("Invalid token");
        }
    }

    private Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(config.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
