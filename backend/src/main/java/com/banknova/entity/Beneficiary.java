package com.banknova.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "beneficiaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String beneficiaryName;

    @Column(nullable = false)
    private String beneficiaryEmail;

    @Column
    private String beneficiaryPhone;

    @Column(nullable = false)
    private String accountNumber; // Can be internal (BN- format) or external

    @Column
    private String bankCode; // For external transfers (e.g., ROUTING_NUMBER)

    @Column
    private String bankName; // For external transfers

    @Column(nullable = false)
    private String relationship; // FRIEND, FAMILY, WORK, OTHER

    @Column(nullable = false)
    private Boolean isVerified = false;

    @Column
    private LocalDateTime verificationDate;

    @Column
    private Boolean isActive = true;

    @Column
    private String verificationToken; // For email verification

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
