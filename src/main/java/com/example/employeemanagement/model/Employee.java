package com.example.employeemanagement.model;

public record Employee(
        Long id,
        String code,
        String name,
        String email,
        String department) {
}