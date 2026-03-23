package com.banknova.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banknova.entity.Account;
import com.banknova.entity.Loan;
import com.banknova.entity.Loan.LoanStatus;
import com.banknova.entity.Loan.LoanType;
import com.banknova.entity.User;
import com.banknova.exception.BankNovaException;
import com.banknova.repository.LoanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository repository;

    /**
     * Create a new loan (In production, this would involve credit checks,
     * approvals, etc.)
     */
    @Transactional
    public Loan createLoan(
            User user,
            Account account,
            BigDecimal principalAmount,
            BigDecimal annualInterestRate,
            Integer loanTermMonths,
            LoanType loanType,
            String loanPurpose) {
        if (principalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankNovaException("Loan amount must be greater than zero");
        }

        if (annualInterestRate.compareTo(BigDecimal.ZERO) < 0 || annualInterestRate.compareTo(new BigDecimal(50)) > 0) {
            throw new BankNovaException("Annual interest rate must be between 0 and 50%");
        }

        if (loanTermMonths < 1 || loanTermMonths > 360) {
            throw new BankNovaException("Loan term must be between 1 and 360 months");
        }

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setLinkedAccount(account);
        loan.setLoanNumber(generateLoanNumber());
        loan.setPrincipalAmount(principalAmount);
        loan.setOutstandingBalance(principalAmount);
        loan.setAnnualInterestRate(annualInterestRate);
        loan.setLoanTermMonths(loanTermMonths);
        loan.setLoanType(loanType);
        loan.setLoanPurpose(loanPurpose);
        loan.setStatus(LoanStatus.APPROVED);
        loan.setDisbursementDate(java.time.LocalDateTime.now());
        loan.setNextPaymentDueDate(java.time.LocalDateTime.now().plusMonths(1).withDayOfMonth(1));

        // Calculate monthly payment using standard loan formula
        BigDecimal monthlyPayment = loan.calculateMonthlyPayment();
        loan.setMonthlyPayment(monthlyPayment);

        // Disburse loan amount to account
        account.setBalance(account.getBalance().add(principalAmount));

        return repository.save(loan);
    }

    /**
     * Get all loans for a user
     */
    public List<Loan> getUserLoans(User user) {
        return repository.findByUser(user);
    }

    /**
     * Get active loans only
     */
    public List<Loan> getActiveLoans(User user) {
        return repository.findActiveLoans(user);
    }

    /**
     * Make a loan payment
     */
    @Transactional
    public Loan makeLoanPayment(User user, Long loanId, BigDecimal paymentAmount) {
        Loan loan = repository.findById(loanId)
                .orElseThrow(() -> new BankNovaException("Loan not found"));

        if (!loan.getUser().getId().equals(user.getId())) {
            throw new BankNovaException("Unauthorized: This loan does not belong to you");
        }

        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new BankNovaException("This loan is not active");
        }

        if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankNovaException("Payment amount must be greater than zero");
        }

        if (paymentAmount.compareTo(loan.getOutstandingBalance()) > 0) {
            throw new BankNovaException("Payment amount exceeds outstanding balance");
        }

        // Update loan
        loan.setOutstandingBalance(loan.getOutstandingBalance().subtract(paymentAmount));
        loan.setTotalAmountPaid(loan.getTotalAmountPaid().add(paymentAmount));
        loan.setLastPaymentDate(java.time.LocalDateTime.now());

        // Interest calculation (simplified - 30% of payment goes to interest)
        BigDecimal interestPortion = paymentAmount.multiply(new BigDecimal("0.3"));
        loan.setTotalInterestPaid(loan.getTotalInterestPaid().add(interestPortion));

        // Mark as paid off if balance is zero
        if (loan.getOutstandingBalance().compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus(LoanStatus.PAID_OFF);
        } else {
            loan.setNextPaymentDueDate(java.time.LocalDateTime.now().plusMonths(1).withDayOfMonth(1));
        }

        return repository.save(loan);
    }

    /**
     * Get outstanding balance for a loan
     */
    public BigDecimal getOutstandingBalance(Long loanId) {
        return repository.findById(loanId)
                .map(Loan::getOutstandingBalance)
                .orElseThrow(() -> new BankNovaException("Loan not found"));
    }

    /**
     * Calculate total interest due
     */
    public BigDecimal calculateTotalInterestDue(Long loanId) {
        Loan loan = repository.findById(loanId)
                .orElseThrow(() -> new BankNovaException("Loan not found"));

        return loan.getMonthlyPayment()
                .multiply(new BigDecimal(loan.getLoanTermMonths()))
                .subtract(loan.getPrincipalAmount());
    }

    private String generateLoanNumber() {
        return "LN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
