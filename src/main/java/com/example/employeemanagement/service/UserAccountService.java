package com.example.employeemanagement.service;

import java.util.Locale;

import com.example.employeemanagement.dto.RegisterUserRequest;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.model.AppUser;
import com.example.employeemanagement.model.Role;
import com.example.employeemanagement.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserAccountService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(
            RegisterUserRequest request) {

        String username =
                normalizeUsername(request.username());

        if (appUserRepository
                .existsByUsername(username)) {
            throw new DuplicateResourceException(
                    "Username already exists");
        }

        AppUser appUser = new AppUser(
                username,
                passwordEncoder.encode(
                        request.password()),
                Role.USER);

        appUserRepository.save(appUser);
    }

    @Transactional
    public void createIfMissing(
            String username,
            String rawPassword,
            Role role) {

        String normalizedUsername =
                normalizeUsername(username);

        if (appUserRepository
                .existsByUsername(normalizedUsername)) {
            return;
        }

        appUserRepository.save(
                new AppUser(
                        normalizedUsername,
                        passwordEncoder.encode(
                                rawPassword),
                        role));
    }

    private String normalizeUsername(
            String username) {

        return username
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}