package com.example.employeemanagement.service;

import java.util.Locale;

import com.example.employeemanagement.model.AppUser;
import com.example.employeemanagement.repository.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService
        implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public CustomUserDetailsService(
            AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String username) {

        String normalizedUsername =
                username.trim()
                        .toLowerCase(Locale.ROOT);

        AppUser appUser = appUserRepository
                .findByUsername(normalizedUsername)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Username or password is invalid"));

        return User.withUsername(
                        appUser.getUsername())
                .password(
                        appUser.getPasswordHash())
                .roles(
                        appUser.getRole().name())
                .build();
    }
}