package com.example.employeemanagement.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Configuration(proxyBeanMethods = false)
public class JwtConfiguration {

    @Bean
    public SecretKey jwtSecretKey(
            @Value("${app.security.jwt.secret}")
            String secret) {

        byte[] keyBytes =
                secret.getBytes(
                        StandardCharsets.UTF_8);

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must contain at least 32 bytes");
        }

        return new SecretKeySpec(
                keyBytes,
                "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(
            SecretKey secretKey) {

        JWK jwk =
                new OctetSequenceKey.Builder(
                        secretKey)
                        .algorithm(
                                JWSAlgorithm.HS256)
                        .build();

        JWKSource<SecurityContext> jwkSource =
                new ImmutableJWKSet<>(
                        new JWKSet(jwk));

        return new NimbusJwtEncoder(
                jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(
            SecretKey secretKey,
            @Value("${app.security.jwt.issuer}")
            String issuer) {

        NimbusJwtDecoder decoder =
                NimbusJwtDecoder
                        .withSecretKey(secretKey)
                        .macAlgorithm(
                                MacAlgorithm.HS256)
                        .build();

        decoder.setJwtValidator(
                JwtValidators
                        .createDefaultWithIssuer(
                                issuer));

        return decoder;
    }

    @Bean
    public JwtAuthenticationConverter
    jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter
                authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter
                .setAuthoritiesClaimName(
                        "roles");

        authoritiesConverter
                .setAuthorityPrefix(
                        "ROLE_");

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                authoritiesConverter);

        return converter;
    }
}