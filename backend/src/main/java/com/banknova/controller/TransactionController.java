package com.banknova.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banknova.config.RateLimited;
import com.banknova.dto.AnalyticsDto;
import com.banknova.dto.SendMoneyRequest;
import com.banknova.dto.TransactionDto;
import com.banknova.service.AnalyticsService;
import com.banknova.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transaction management APIs for money transfers and history")
@SecurityRequirement(name = "Bearer Authentication")
public class TransactionController {

    private final TransactionService transactionService;
    private final AnalyticsService analyticsService;

    @PostMapping("/send")
    @RateLimited(requests = 5, timeWindowSeconds = 300) // 5 transfers per 5 minutes
    @Operation(summary = "Send money to another user", description = "Transfers money from authenticated user to recipient. Rate limited to 5 transfers per 5 minutes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Money sent successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data or insufficient balance", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recipient not found", content = @Content),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded", content = @Content)
    })
    public ResponseEntity<TransactionDto> sendMoney(
            @Valid @RequestBody SendMoneyRequest request,
            Authentication authentication) {
        String senderEmail = authentication.getName();
        TransactionDto transaction = transactionService.sendMoney(senderEmail, request);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/history")
    @Operation(summary = "Get transaction history", description = "Retrieves all transactions for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction history retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDto.class)))
    })
    public ResponseEntity<List<TransactionDto>> getTransactionHistory(Authentication authentication) {
        String email = authentication.getName();
        List<TransactionDto> transactions = transactionService.getTransactionHistory(email);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/history/paginated")
    @Operation(summary = "Get paginated transaction history", description = "Retrieves paginated transaction history with sorting options")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated transaction history retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<TransactionDto>> getTransactionHistoryPaginated(
            Authentication authentication,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "timestamp") @RequestParam(defaultValue = "timestamp") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "desc") @RequestParam(defaultValue = "desc") String sortDir) {

        String email = authentication.getName();
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TransactionDto> transactions = transactionService.getTransactionHistoryPaginated(email, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get user analytics", description = "Retrieves financial analytics and metrics for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Analytics retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnalyticsDto.class)))
    })
    public ResponseEntity<AnalyticsDto> getAnalytics(Authentication authentication) {
        String email = authentication.getName();
        AnalyticsDto analytics = analyticsService.getUserAnalytics(email);
        return ResponseEntity.ok(analytics);
    }
}
