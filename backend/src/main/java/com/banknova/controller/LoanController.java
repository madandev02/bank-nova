package com.banknova.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banknova.dto.LoanDTO;
import com.banknova.entity.Account;
import com.banknova.entity.Loan;
import com.banknova.entity.Loan.LoanType;
import com.banknova.entity.User;
import com.banknova.exception.BankNovaException;
import com.banknova.repository.AccountRepository;
import com.banknova.repository.UserRepository;
import com.banknova.service.EmailService;
import com.banknova.service.LoanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
@Tag(name = "Credit Products", description = "Loan and credit management")
@SecurityRequirement(name = "Bearer Authentication")
public class LoanController {

    private final LoanService loanService;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;

    @PostMapping("/apply")
    @Operation(summary = "Apply for a loan", description = "Submit a loan application (auto-approved for demo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Loan approved and created"),
            @ApiResponse(responseCode = "400", description = "Invalid loan details"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<LoanDTO>> applyForLoan(
            Authentication authentication,
            @Valid @RequestBody CreateLoanRequest request) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new BankNovaException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new BankNovaException("Unauthorized: This account does not belong to you");
        }

        Loan loan = loanService.createLoan(
                user,
                account,
                request.getPrincipalAmount(),
                request.getAnnualInterestRate(),
                request.getLoanTermMonths(),
                LoanType.valueOf(request.getLoanType()),
                request.getLoanPurpose());

        emailService.sendLoanApprovalEmail(
                user.getEmail(),
                user.getName(),
                loan.getLoanNumber(),
                loan.getPrincipalAmount().toString());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.banknova.dto.ApiResponse.success(convertToDTO(loan), "Loan approved successfully"));
    }

    @GetMapping
    @Operation(summary = "Get user's loans", description = "Retrieve all loans for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loans retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<List<LoanDTO>>> getUserLoans(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        List<LoanDTO> loans = loanService.getUserLoans(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(com.banknova.dto.ApiResponse.success(loans, "Loans retrieved successfully"));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active loans", description = "Retrieve only active loan accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active loans retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<List<LoanDTO>>> getActiveLoans(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        List<LoanDTO> loans = loanService.getActiveLoans(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(com.banknova.dto.ApiResponse.success(loans, "Active loans retrieved successfully"));
    }

    @GetMapping("/{loanId}")
    @Operation(summary = "Get loan details", description = "Retrieve detailed information about a specific loan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan details retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<LoanDTO>> getLoanDetails(
            Authentication authentication,
            @PathVariable Long loanId) {
        // Service can be extended to return single loan by ID
        return ResponseEntity.ok(com.banknova.dto.ApiResponse.success(null, "Loan details retrieved"));
    }

    @PostMapping("/{loanId}/pay")
    @Operation(summary = "Make loan payment", description = "Make a payment towards loan balance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payment amount"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<LoanDTO>> makeLoanPayment(
            Authentication authentication,
            @PathVariable Long loanId,
            @RequestBody LoanPaymentRequest request) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        Loan loan = loanService.makeLoanPayment(user, loanId, request.getPaymentAmount());

        return ResponseEntity
                .ok(com.banknova.dto.ApiResponse.success(convertToDTO(loan), "Payment processed successfully"));
    }

    private LoanDTO convertToDTO(Loan loan) {
        return LoanDTO.builder()
                .id(loan.getId())
                .loanNumber(loan.getLoanNumber())
                .principalAmount(loan.getPrincipalAmount())
                .outstandingBalance(loan.getOutstandingBalance())
                .annualInterestRate(loan.getAnnualInterestRate())
                .loanTermMonths(loan.getLoanTermMonths())
                .monthlyPayment(loan.getMonthlyPayment())
                .loanType(loan.getLoanType().toString())
                .status(loan.getStatus().toString())
                .loanPurpose(loan.getLoanPurpose())
                .disbursementDate(loan.getDisbursementDate())
                .nextPaymentDueDate(loan.getNextPaymentDueDate())
                .totalInterestPaid(loan.getTotalInterestPaid())
                .totalAmountPaid(loan.getTotalAmountPaid())
                .build();
    }
}

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class CreateLoanRequest {
    private Long accountId;
    private java.math.BigDecimal principalAmount;
    private java.math.BigDecimal annualInterestRate;
    private Integer loanTermMonths;
    private String loanType;
    private String loanPurpose;
}

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class LoanPaymentRequest {
    private java.math.BigDecimal paymentAmount;
}
