package com.banknova.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpendingLimitDTO {
    private Long id;
    private String category;
    private String limitType;
    private BigDecimal limitAmount;
    private BigDecimal currentSpent;
    private BigDecimal remainingBudget;
    private Boolean isActive;
    private LocalDateTime createdAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SetSpendingLimitRequest {
    private String category; // FOOD, TRANSPORT, ENTERTAINMENT, UTILITIES, TRANSFER, INVESTMENT, OTHER
    private String limitType; // DAILY, WEEKLY, MONTHLY, YEARLY
    private BigDecimal limitAmount;
}
