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
public class LoanDTO {
    private Long id;
    private String loanNumber;
    private BigDecimal principalAmount;
    private BigDecimal outstandingBalance;
    private BigDecimal annualInterestRate;
    private Integer loanTermMonths;
    private BigDecimal monthlyPayment;
    private String loanType;
    private String status;
    private String loanPurpose;
    private LocalDateTime disbursementDate;
    private LocalDateTime nextPaymentDueDate;
    private BigDecimal totalInterestPaid;
    private BigDecimal totalAmountPaid;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CreateLoanRequest {
    private Long accountId;
    private BigDecimal principalAmount;
    private BigDecimal annualInterestRate;
    private Integer loanTermMonths;
    private String loanType; // PERSONAL, AUTO, HOME, EDUCATION
    private String loanPurpose;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class LoanPaymentRequest {
    private BigDecimal paymentAmount;
}
