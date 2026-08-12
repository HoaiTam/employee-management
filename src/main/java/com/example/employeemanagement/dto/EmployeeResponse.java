package com.example.employeemanagement.dto;

public record EmployeeResponse(
        Long id,
        String code,
        String name,
        String email,
        DepartmentResponse department) {
}