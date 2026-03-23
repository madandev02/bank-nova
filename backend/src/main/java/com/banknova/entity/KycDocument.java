package com.banknova.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kyc_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KycDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String documentType; // PASSPORT, DRIVER_LICENSE, NATIONAL_ID, AADHAR

    @Column(nullable = false)
    private String documentNumber;

    @Column
    private String documentUrl; // URL to uploaded document

    @Column
    private String country;

    @Column
    private String issuedDate;

    @Column
    private String expiryDate;

    @Column(nullable = false)
    private String verificationStatus = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column
    private String rejectionReason;

    @CreationTimestamp
    private LocalDateTime submittedAt;

    @Column
    private LocalDateTime verifiedAt;

    @Column
    private String verifiedByAdmin;

    @Column
    private String additionalNotes;
}
