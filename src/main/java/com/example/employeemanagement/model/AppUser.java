package com.example.employeemanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "app_users",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_app_users_username",
                columnNames = "username"))
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            length = 50)
    private String username;

    @Column(
            name = "password_hash",
            nullable = false,
            length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20)
    private Role role;

    protected AppUser() {
    }

    public AppUser(
            String username,
            String passwordHash,
            Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }
}