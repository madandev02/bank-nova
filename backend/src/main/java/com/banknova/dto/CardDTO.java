package com.banknova.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {
    private Long id;
    private String last4Digits;
    private String cardholderName;
    private String expiryDate;
    private String cardNetwork;
    private String cardType;
    private String status;
    private Boolean isDefault;
    private BigDecimal dailyLimit;
    private BigDecimal monthlyLimit;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AddCardRequest {
    private Long accountId;
    private String cardNumber;
    private String cardholderName;
    private String expiryDate; // Format: MM/YY
    private String cvv;
    private String cardNetwork;
    private String cardType; // DEBIT, CREDIT, PREPAID
    private BigDecimal dailyLimit;
    private BigDecimal monthlyLimit;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UpdateCardLimitsRequest {
    private BigDecimal dailyLimit;
    private BigDecimal monthlyLimit;
}
