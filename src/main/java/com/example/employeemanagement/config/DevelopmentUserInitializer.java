package com.example.employeemanagement.config;

import com.example.employeemanagement.model.Role;
import com.example.employeemanagement.service.UserAccountService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevelopmentUserInitializer
        implements ApplicationRunner {

    private final UserAccountService userAccountService;
    private final String adminPassword;
    private final String userPassword;

    public DevelopmentUserInitializer(
            UserAccountService userAccountService,
            @Value("${app.security.dev-users.admin-password}")
            String adminPassword,
            @Value("${app.security.dev-users.user-password}")
            String userPassword) {
        this.userAccountService =
                userAccountService;
        this.adminPassword = adminPassword;
        this.userPassword = userPassword;
    }

    @Override
    public void run(
            ApplicationArguments args) {

        userAccountService.createIfMissing(
                "admin",
                adminPassword,
                Role.ADMIN);

        userAccountService.createIfMissing(
                "user",
                userPassword,
                Role.USER);
    }
}