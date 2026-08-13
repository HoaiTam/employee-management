package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.RegisterUserRequest;
import com.example.employeemanagement.model.AppUser;
import com.example.employeemanagement.model.Role;
import com.example.employeemanagement.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
class UserAccountServiceTest {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registrationCreatesUserWithEncodedPassword() {

        String username =
                "test-" + UUID.randomUUID();
        String rawPassword =
                "StrongPassword123!";

        userAccountService.register(
                new RegisterUserRequest(
                        username,
                        rawPassword));

        AppUser saved = appUserRepository
                .findByUsername(username)
                .orElseThrow();

        assertThat(saved.getRole())
                .isEqualTo(Role.USER);

        assertThat(saved.getPasswordHash())
                .isNotEqualTo(rawPassword);

        assertThat(
                passwordEncoder.matches(
                        rawPassword,
                        saved.getPasswordHash()))
                .isTrue();
    }
}