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
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

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
    private String loanNumber; // Format: LN-XXXXXX-NUMBERS

    @Column(nullable = false)
    private BigDecimal principalAmount; // Original loan amount

    @Column(nullable = false)
    private BigDecimal outstandingBalance; // Remaining balance

    @Column(nullable = false)
    private BigDecimal annualInterestRate; // e.g., 5.5 for 5.5%

    @Column(nullable = false)
    private Integer loanTermMonths; // Duration in months

    @Column(nullable = false)
    private BigDecimal monthlyPayment;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoanType loanType; // PERSONAL, AUTO, HOME, EDUCATION

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoanStatus status; // APPROVED, ACTIVE, PAID_OFF, DEFAULT

    @Column
    private String loanPurpose; // Description of what loan is for

    @Column(nullable = false)
    private LocalDateTime disbursementDate; // When funds were released

    @Column(nullable = false)
    private LocalDateTime nextPaymentDueDate;

    @Column
    private LocalDateTime lastPaymentDate;

    @Column
    private Integer missedPayments = 0;

    @Column
    private BigDecimal totalInterestPaid = BigDecimal.ZERO;

    @Column
    private BigDecimal totalAmountPaid = BigDecimal.ZERO;

    @Column
    private BigDecimal collateralValue; // For secured loans

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum LoanType {
        PERSONAL, AUTO, HOME, EDUCATION
    }

    public enum LoanStatus {
        APPROVED, ACTIVE, PAID_OFF, DEFAULT
    }

    public BigDecimal calculateMonthlyPayment() {
        if (annualInterestRate.compareTo(BigDecimal.ZERO) == 0) {
            return principalAmount.divide(new BigDecimal(loanTermMonths), 2, java.math.RoundingMode.HALF_UP);
        }

        BigDecimal monthlyRate = annualInterestRate.divide(new BigDecimal(100 * 12), 10,
                java.math.RoundingMode.HALF_UP);
        BigDecimal numerator = monthlyRate.multiply(principalAmount).multiply(
                monthlyRate.add(BigDecimal.ONE).pow(loanTermMonths));
        BigDecimal denominator = monthlyRate.add(BigDecimal.ONE).pow(loanTermMonths).subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, java.math.RoundingMode.HALF_UP);
    }
}
