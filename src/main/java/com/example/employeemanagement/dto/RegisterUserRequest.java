package com.example.employeemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(

        @NotBlank(message = "Username is required")
        @Size(
                min = 3,
                max = 50,
                message = "Username must contain between 3 and 50 characters")
        @Pattern(
                regexp = "[a-zA-Z0-9._-]+",
                message = "Username contains invalid characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(
                min = 8,
                max = 72,
                message = "Password must contain between 8 and 72 characters")
        String password) {
}