package com.banknova.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Table(name = "spending_limits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpendingLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LimitType limitType; // DAILY, WEEKLY, MONTHLY, YEARLY

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionCategory category; // FOOD, TRANSPORT, ENTERTAINMENT, UTILITIES, OTHER

    @Column(nullable = false)
    private BigDecimal limitAmount;

    @Column(nullable = false)
    private BigDecimal currentSpent = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column
    private LocalDateTime resetDate; // When limit resets

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum LimitType {
        DAILY, WEEKLY, MONTHLY, YEARLY
    }

    public enum TransactionCategory {
        FOOD, TRANSPORT, ENTERTAINMENT, UTILITIES, TRANSFER, INVESTMENT, OTHER
    }

    public boolean isLimitExceeded(BigDecimal amount) {
        return currentSpent.add(amount).compareTo(limitAmount) > 0;
    }

    public void resetIfNeeded() {
        if (resetDate != null && LocalDateTime.now().isAfter(resetDate)) {
            currentSpent = BigDecimal.ZERO;
            updateResetDate();
        }
    }

    public void updateResetDate() {
        LocalDateTime now = LocalDateTime.now();
        switch (limitType) {
            case DAILY -> resetDate = now.plusDays(1).withHour(0).withMinute(0).withSecond(0);
            case WEEKLY -> resetDate = now.plusWeeks(1).withHour(0).withMinute(0).withSecond(0);
            case MONTHLY -> resetDate = now.plusMonths(1).withHour(0).withMinute(0).withSecond(0);
            case YEARLY -> resetDate = now.plusYears(1).withHour(0).withMinute(0).withSecond(0);
        }
    }
}
