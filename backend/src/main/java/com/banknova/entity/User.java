package com.banknova.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "provider")
    private String provider; // google, apple, phone, local

    // Profile information
    @Column
    private String phoneNumber;

    @Column
    private String profilePictureUrl;

    @Column
    private String dateOfBirth;

    @Column
    private String country;

    @Column
    private String city;

    @Column
    private String address;

    // Verification fields
    @Column(nullable = false)
    private Boolean emailVerified = false;

    @Column(nullable = false)
    private Boolean phoneVerified = false;

    @Column(nullable = false)
    private Boolean twoFactorEnabled = false;

    @Column(length = 20)
    private String kycStatus = "PENDING"; // PENDING, VERIFIED, REJECTED

    @Column(length = 20)
    private String accountStatus = "ACTIVE"; // ACTIVE, SUSPENDED, CLOSED

    @Column
    private String kycDocument; // URL to KYC document

    @Column
    private LocalDateTime kycVerificationDate;

    // Login tracking
    @Column
    private LocalDateTime lastLogin;

    @Column
    private LocalDateTime lastPasswordChange;

    @Column
    private Integer loginAttempts = 0; // For account lockout

    @Column
    private LocalDateTime accountLockedUntil;

    // Audit fields
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    private String lastKnownIp; // IP address of last login
}
