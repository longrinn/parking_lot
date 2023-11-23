package com.endava.internship.infrastructure.security;

import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;

@Component
public class JwtUtils {

    private final String secret;

    public JwtUtils(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    public String generateToken(String email) {
        Date expiredDate = Date.from(ZonedDateTime.now().plusHours(24).toInstant());

        return JWT.create()
                .withSubject("User details")
                .withClaim("email", email)
                .withIssuedAt(new Date())
                .withIssuer("PARKING_LOT")
                .withExpiresAt(expiredDate)
                .sign(HMAC256(secret));
    }

    public String validateTokenAndRetrieveClaim(String token) throws JWTVerificationException {
        final JWTVerifier verifier = JWT.require(HMAC256(secret))
                .build();

        final DecodedJWT decodedJWT = verifier.verify(token);

        return decodedJWT.getClaim("email").asString();
    }
}
