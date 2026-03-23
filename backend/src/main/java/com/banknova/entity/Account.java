package com.banknova.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String accountNumber; // Format: BN-XXXXXX-NUMBERS

    @Column(nullable = false)
    private String accountType; // CHECKING, SAVINGS, BUSINESS

    @Column(nullable = false)
    private String accountName; // User-friendly name

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column
    private String currency = "USD";

    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE, SUSPENDED, CLOSED

    @Column
    private BigDecimal dailyWithdrawalLimit = new BigDecimal("5000.00");

    @Column
    private BigDecimal monthlyTransferLimit = new BigDecimal("50000.00");

    @Column
    private BigDecimal currentMonthlyTransferred = BigDecimal.ZERO;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime closedAt;
}
