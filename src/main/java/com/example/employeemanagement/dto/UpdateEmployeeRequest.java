package com.example.employeemanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateEmployeeRequest(

        @NotBlank(message = "Name is required")
        @Size(
                min = 2,
                max = 150,
                message = "Name must contain between 2 and 150 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(
                max = 255,
                message = "Email must not exceed 255 characters")
        String email,

        @NotNull(message = "Department id is required")
        @Positive(message = "Department id must be greater than 0")
        Long departmentId) {
}