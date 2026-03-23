package com.banknova.entity;

import java.time.LocalDateTime;
import java.time.YearMonth;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account linkedAccount;

    @Column(nullable = false, unique = true)
    private String tokenizedCardNumber; // Encrypted, last 4 digits are appended

    @Column(nullable = false)
    private String last4Digits; // For display only (e.g., "4242")

    @Column(nullable = false)
    private String cardholderName;

    @Column(nullable = false)
    private YearMonth expiryDate; // Format: 2026-03

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CardType cardType; // DEBIT, CREDIT, PREPAID

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CardStatus status; // ACTIVE, BLOCKED, EXPIRED, CLOSED

    @Column
    private String cardNetwork; // VISA, MASTERCARD, AMEX

    @Column
    private Boolean isDefault = false;

    @Column(name = "daily_limit")
    private java.math.BigDecimal dailyLimit; // Max spending per day

    @Column(name = "monthly_limit")
    private java.math.BigDecimal monthlyLimit; // Max spending per month

    @Column(name = "current_daily_spent")
    private java.math.BigDecimal currentDailySpent = java.math.BigDecimal.ZERO;

    @Column(name = "current_monthly_spent")
    private java.math.BigDecimal currentMonthlySpent = java.math.BigDecimal.ZERO;

    @Column(name = "is_international_enabled")
    private Boolean isInternationalEnabled = true;

    @Column(name = "is_contactless_enabled")
    private Boolean isContactlessEnabled = true;

    @Column(name = "is_online_enabled")
    private Boolean isOnlineEnabled = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deactivatedAt;

    public enum CardType {
        DEBIT, CREDIT, PREPAID
    }

    public enum CardStatus {
        ACTIVE, BLOCKED, EXPIRED, CLOSED
    }
}
