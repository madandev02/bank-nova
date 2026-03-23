package com.banknova.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "transfer_verifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String verificationCode; // 6-digit OTP

    @Column(nullable = false)
    private String transferDetails; // JSON or serialized transfer info

    @Column(nullable = false)
    private Long transferId; // Reference to intended transaction

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationStatus status; // PENDING, VERIFIED, EXPIRED, CANCELLED

    @Column(nullable = false)
    private String verificationMethod; // EMAIL, SMS, AUTHENTICATOR_APP

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiryTime; // OTP expires after 10 minutes

    @Column
    private LocalDateTime verifiedAt;

    @Column
    private Integer attemptCount = 0;

    @Column
    private LocalDateTime lastAttemptTime;

    public enum VerificationStatus {
        PENDING, VERIFIED, EXPIRED, CANCELLED
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

    public boolean isCodeValid(String code) {
        return !isExpired() && this.verificationCode.equals(code) && status == VerificationStatus.PENDING;
    }
}
