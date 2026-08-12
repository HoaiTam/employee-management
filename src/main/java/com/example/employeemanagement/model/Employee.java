package com.example.employeemanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "employees",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_employees_email",
                columnNames = "email"))
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            length = 150)
    private String name;

    @Column(
            nullable = false,
            length = 255)
    private String email;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false)
    @JoinColumn(
            name = "department_id",
            nullable = false)
    private Department department;

    protected Employee() {
    }

    public Employee(
            String name,
            String email,
            Department department) {
        this.name = name;
        this.email = email;
        this.department = department;
    }

    public void updateDetails(
            String name,
            String email,
            Department department) {
        this.name = name;
        this.email = email;
        this.department = department;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Department getDepartment() {
        return department;
    }
}