package com.example.employeemanagement.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.example.employeemanagement.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    private static final String ROLE_PREFIX =
            "ROLE_";

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final Duration accessTokenTtl;

    public JwtTokenService(
            JwtEncoder jwtEncoder,
            @Value("${app.security.jwt.issuer}")
            String issuer,
            @Value("${app.security.jwt.access-token-ttl}")
            Duration accessTokenTtl) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.accessTokenTtl = accessTokenTtl;
    }

    public TokenResponse createToken(
            Authentication authentication) {

        Instant issuedAt = Instant.now();
        Instant expiresAt =
                issuedAt.plus(accessTokenTtl);

        List<String> roles =
                authentication
                        .getAuthorities()
                        .stream()
                        .map(
                                GrantedAuthority
                                        ::getAuthority)
                        .filter(authority ->
                                authority.startsWith(
                                        ROLE_PREFIX))
                        .map(authority ->
                                authority.substring(
                                        ROLE_PREFIX.length()))
                        .toList();

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .issuer(issuer)
                        .issuedAt(issuedAt)
                        .expiresAt(expiresAt)
                        .subject(
                                authentication.getName())
                        .claim("roles", roles)
                        .build();

        JwsHeader header =
                JwsHeader
                        .with(MacAlgorithm.HS256)
                        .build();

        String token = jwtEncoder
                .encode(
                        JwtEncoderParameters.from(
                                header,
                                claims))
                .getTokenValue();

        return new TokenResponse(
                token,
                "Bearer",
                accessTokenTtl.toSeconds());
    }
}