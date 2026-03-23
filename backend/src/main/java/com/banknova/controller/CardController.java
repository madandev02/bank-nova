package com.banknova.controller;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banknova.dto.CardDTO;
import com.banknova.entity.Account;
import com.banknova.entity.Card.CardType;
import com.banknova.entity.User;
import com.banknova.exception.BankNovaException;
import com.banknova.repository.AccountRepository;
import com.banknova.repository.UserRepository;
import com.banknova.service.CardService;
import com.banknova.service.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
@Tag(name = "Card Management", description = "Card management and payment methods")
@SecurityRequirement(name = "Bearer Authentication")
public class CardController {

    private final CardService cardService;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;

    @PostMapping("/add")
    @Operation(summary = "Add a new card", description = "Securely add a new card to user's account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid card details"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<CardDTO>> addCard(
            Authentication authentication,
            @Valid @RequestBody AddCardRequest request) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new BankNovaException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new BankNovaException("Unauthorized: This account does not belong to you");
        }

        var card = cardService.addCard(
                user,
                account,
                request.getCardNumber(),
                request.getCardholderName(),
                YearMonth.parse("20" + request.getExpiryDate().replace("/", "-")),
                request.getCardNetwork(),
                CardType.valueOf(request.getCardType()),
                request.getDailyLimit(),
                request.getMonthlyLimit());

        emailService.sendCardActivationEmail(user.getEmail(), user.getName(), card.getLast4Digits());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.banknova.dto.ApiResponse.success(CardDTO.builder()
                        .id(card.getId())
                        .last4Digits(card.getLast4Digits())
                        .cardholderName(card.getCardholderName())
                        .expiryDate(card.getExpiryDate().toString())
                        .cardNetwork(card.getCardNetwork())
                        .cardType(card.getCardType().toString())
                        .status(card.getStatus().toString())
                        .isDefault(card.getIsDefault())
                        .build(), "Card added successfully"));
    }

    @GetMapping
    @Operation(summary = "Get user's cards", description = "Retrieve all cards for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<List<CardDTO>>> getUserCards(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        List<CardDTO> cards = cardService.getUserCards(user);
        return ResponseEntity.ok(com.banknova.dto.ApiResponse.success(cards, "Cards retrieved successfully"));
    }

    @PatchMapping("/{cardId}/default")
    @Operation(summary = "Set default card", description = "Set a card as the default payment method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Default card set successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<String>> setDefaultCard(
            Authentication authentication,
            @PathVariable Long cardId) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        cardService.setDefaultCard(user, cardId);
        return ResponseEntity.ok(com.banknova.dto.ApiResponse.success("Default card set successfully"));
    }

    @PostMapping("/{cardId}/block")
    @Operation(summary = "Block a card", description = "Block a card due to security concerns (fraud)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card blocked successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<String>> blockCard(
            Authentication authentication,
            @PathVariable Long cardId) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        cardService.blockCard(user, cardId);
        return ResponseEntity.ok(com.banknova.dto.ApiResponse.success("Card blocked successfully"));
    }

    @DeleteMapping("/{cardId}")
    @Operation(summary = "Close a card", description = "Permanently close a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card closed successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<String>> closeCard(
            Authentication authentication,
            @PathVariable Long cardId) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        cardService.closeCard(user, cardId);
        return ResponseEntity.ok(com.banknova.dto.ApiResponse.success("Card closed successfully"));
    }

    @PutMapping("/{cardId}/limits")
    @Operation(summary = "Update card limits", description = "Update daily and monthly spending limits for a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Limits updated successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<String>> updateCardLimits(
            Authentication authentication,
            @PathVariable Long cardId,
            @RequestBody UpdateCardLimitsRequest request) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        cardService.updateCardLimits(user, cardId, request.getDailyLimit(), request.getMonthlyLimit());
        return ResponseEntity.ok(com.banknova.dto.ApiResponse.success("Card limits updated successfully"));
    }
}

// Request DTOs
@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class AddCardRequest {
    private Long accountId;
    private String cardNumber;
    private String cardholderName;
    private String expiryDate;
    private String cvv;
    private String cardNetwork;
    private String cardType;
    private BigDecimal dailyLimit;
    private BigDecimal monthlyLimit;
}

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class UpdateCardLimitsRequest {
    private BigDecimal dailyLimit;
    private BigDecimal monthlyLimit;
}
