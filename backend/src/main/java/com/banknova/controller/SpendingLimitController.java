package com.banknova.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banknova.dto.SpendingLimitDTO;
import com.banknova.entity.Account;
import com.banknova.entity.SpendingLimit;
import com.banknova.entity.SpendingLimit.LimitType;
import com.banknova.entity.SpendingLimit.TransactionCategory;
import com.banknova.entity.User;
import com.banknova.exception.BankNovaException;
import com.banknova.repository.AccountRepository;
import com.banknova.repository.UserRepository;
import com.banknova.service.SpendingLimitService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/spending-limits")
@RequiredArgsConstructor
@Tag(name = "Spending Controls", description = "Set and manage spending limits by category")
@SecurityRequirement(name = "Bearer Authentication")
public class SpendingLimitController {

    private final SpendingLimitService spendingLimitService;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @PostMapping("/set")
    @Operation(summary = "Set spending limit", description = "Set a spending limit for a specific category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Spending limit created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid limit details"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<SpendingLimitDTO>> setSpendingLimit(
            Authentication authentication,
            @Valid @RequestBody SetSpendingLimitRequest request) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        // Get user's primary account (or you could add accountId to request)
        Account account = accountRepository.findByUserAndAccountType(user, "CHECKING")
                .orElseThrow(() -> new BankNovaException("Checking account not found"));

        SpendingLimit limit = spendingLimitService.setSpendingLimit(
                user,
                account,
                TransactionCategory.valueOf(request.getCategory()),
                LimitType.valueOf(request.getLimitType()),
                request.getLimitAmount());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.banknova.dto.ApiResponse.success(convertToDTO(limit, user),
                        "Spending limit created successfully"));
    }

    @GetMapping
    @Operation(summary = "Get spending limits", description = "Retrieve all active spending limits for the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spending limits retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<List<SpendingLimitDTO>>> getUserSpendingLimits(
            Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        List<SpendingLimitDTO> limits = spendingLimitService.getUserLimits(user).stream()
                .map(limit -> convertToDTO(limit, user))
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(com.banknova.dto.ApiResponse.success(limits, "Spending limits retrieved successfully"));
    }

    @GetMapping("/budget/{category}")
    @Operation(summary = "Get category budget", description = "Check remaining budget for a specific spending category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Budget information retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<BudgetResponse>> getRemainingBudget(
            Authentication authentication,
            @PathVariable String category) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        BigDecimal remaining = spendingLimitService.getRemainingBudget(user, TransactionCategory.valueOf(category));

        return ResponseEntity.ok(com.banknova.dto.ApiResponse.success(
                BudgetResponse.builder()
                        .category(category)
                        .remaining(remaining)
                        .build(),
                "Budget information retrieved"));
    }

    @DeleteMapping("/{limitId}")
    @Operation(summary = "Disable spending limit", description = "Disable a spending limit for a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spending limit disabled successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Limit not found")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<String>> disableSpendingLimit(
            Authentication authentication,
            @PathVariable Long limitId) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        spendingLimitService.disableLimit(user, limitId);
        return ResponseEntity.ok(com.banknova.dto.ApiResponse.success("Spending limit disabled successfully"));
    }

    private SpendingLimitDTO convertToDTO(SpendingLimit limit, User user) {
        BigDecimal remaining = spendingLimitService.getRemainingBudget(user, limit.getCategory());
        return SpendingLimitDTO.builder()
                .id(limit.getId())
                .category(limit.getCategory().toString())
                .limitType(limit.getLimitType().toString())
                .limitAmount(limit.getLimitAmount())
                .currentSpent(limit.getCurrentSpent())
                .remainingBudget(remaining)
                .isActive(limit.getIsActive())
                .createdAt(limit.getCreatedAt())
                .build();
    }
}

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class SetSpendingLimitRequest {
    private String category;
    private String limitType;
    private BigDecimal limitAmount;
}

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class BudgetResponse {
    private String category;
    private BigDecimal remaining;
}
