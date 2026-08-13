package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.LoginRequest;
import com.example.employeemanagement.dto.TokenResponse;
import com.example.employeemanagement.service.JwtTokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public AuthApiController(
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService) {
        this.authenticationManager =
                authenticationManager;
        this.jwtTokenService =
                jwtTokenService;
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> createToken(
            @Valid
            @RequestBody
            LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(),
                                request.password()));

        return ResponseEntity.ok(
                jwtTokenService.createToken(
                        authentication));
    }
}