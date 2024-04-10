package com.lottery.marketplace.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.lottery.marketplace.domain.auth.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${authentication.jwt.secret-key}") // use your property
    private String secret;

    private static final String CLAIM_ROLE = "role";

    private Algorithm getAlgorithmHS() {
        return Algorithm.HMAC512(secret);
    }

    public String generateJwtToken(Authentication authentication) {
        Algorithm algorithmHS = getAlgorithmHS();

        // Collect user's authorities/roles
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(UserRole.ROLE_REGULAR.name()); // default role if no other roles assigned

        JWTCreator.Builder jwtBuilder = JWT.create()
                .withSubject(authentication.getName())
                .withClaim(CLAIM_ROLE, role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date((new Date()).getTime() + 10 * 60 * 60 * 1000));

        return jwtBuilder.sign(algorithmHS);
    }

    public boolean validateJwtToken(String token) {
        Algorithm algorithmHS = getAlgorithmHS();
        try {
            JWTVerifier verifier = JWT.require(algorithmHS)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT != null;
        } catch (JWTVerificationException ex) {
            // Invalid signature or claims
            return false;
        }
    }

    public String getUserNameFromJwtToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getSubject();
    }

}