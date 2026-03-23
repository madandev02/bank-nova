package com.banknova.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banknova.dto.CardDTO;
import com.banknova.entity.Account;
import com.banknova.entity.Card;
import com.banknova.entity.Card.CardStatus;
import com.banknova.entity.Card.CardType;
import com.banknova.entity.User;
import com.banknova.exception.BankNovaException;
import com.banknova.repository.CardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository repository;

    /**
     * Add a new card (securely tokenize and store)
     * In production, use third-party tokenization (Stripe, Adyen, etc.)
     */
    @Transactional
    public Card addCard(
            User user,
            Account account,
            String cardNumber,
            String cardholderName,
            YearMonth expiryDate,
            String cardNetwork,
            CardType cardType,
            BigDecimal dailyLimit,
            BigDecimal monthlyLimit) {
        // Validate card number (basic Luhn check)
        if (!isValidCardNumber(cardNumber)) {
            throw new BankNovaException("Invalid card number");
        }

        // For demo purposesonly: In production, use secure tokenization service
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        String tokenized = "tok_" + System.nanoTime() + "_" + last4;

        Card card = new Card();
        card.setUser(user);
        card.setLinkedAccount(account);
        card.setTokenizedCardNumber(tokenized); // Never store full card number
        card.setLast4Digits(last4);
        card.setCardholderName(cardholderName);
        card.setExpiryDate(expiryDate);
        card.setCardNetwork(cardNetwork);
        card.setCardType(cardType);
        card.setStatus(CardStatus.ACTIVE);
        card.setDailyLimit(dailyLimit);
        card.setMonthlyLimit(monthlyLimit);
        card.setIsDefault(repository.findByUser(user).isEmpty());
        card.setIsInternationalEnabled(true);
        card.setIsContactlessEnabled(true);
        card.setIsOnlineEnabled(true);

        return repository.save(card);
    }

    /**
     * Get user's cards (mask full card number)
     */
    public List<CardDTO> getUserCards(User user) {
        return repository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get active cards only
     */
    public List<Card> getActiveCards(User user) {
        return repository.findByUserAndStatusIn(
                user,
                List.of(CardStatus.ACTIVE));
    }

    /**
     * Set default card for transfers
     */
    @Transactional
    public void setDefaultCard(User user, Long cardId) {
        Card newDefault = repository.findById(cardId)
                .orElseThrow(() -> new BankNovaException("Card not found"));

        if (!newDefault.getUser().getId().equals(user.getId())) {
            throw new BankNovaException("Unauthorized: This card does not belong to you");
        }

        // Unset previous default
        repository.findByUserAndIsDefault(user, true).ifPresent(card -> {
            card.setIsDefault(false);
            repository.save(card);
        });

        // Set new default
        newDefault.setIsDefault(true);
        repository.save(newDefault);
    }

    /**
     * Block a card (fraud protection)
     */
    @Transactional
    public void blockCard(User user, Long cardId) {
        Card card = repository.findById(cardId)
                .orElseThrow(() -> new BankNovaException("Card not found"));

        if (!card.getUser().getId().equals(user.getId())) {
            throw new BankNovaException("Unauthorized: This card does not belong to you");
        }

        card.setStatus(CardStatus.BLOCKED);
        repository.save(card);
    }

    /**
     * Close a card permanently
     */
    @Transactional
    public void closeCard(User user, Long cardId) {
        Card card = repository.findById(cardId)
                .orElseThrow(() -> new BankNovaException("Card not found"));

        if (!card.getUser().getId().equals(user.getId())) {
            throw new BankNovaException("Unauthorized: This card does not belong to you");
        }

        card.setStatus(CardStatus.CLOSED);
        card.setDeactivatedAt(java.time.LocalDateTime.now());
        repository.save(card);
    }

    /**
     * Update card spending limits
     */
    @Transactional
    public void updateCardLimits(User user, Long cardId, BigDecimal dailyLimit, BigDecimal monthlyLimit) {
        Card card = repository.findById(cardId)
                .orElseThrow(() -> new BankNovaException("Card not found"));

        if (!card.getUser().getId().equals(user.getId())) {
            throw new BankNovaException("Unauthorized: This card does not belong to you");
        }

        card.setDailyLimit(dailyLimit);
        card.setMonthlyLimit(monthlyLimit);
        repository.save(card);
    }

    /**
     * Check if transfer amount is within card limits
     */
    public boolean isWithinCardLimits(Card card, BigDecimal amount) {
        if (card.getDailyLimit() != null
                && card.getCurrentDailySpent().add(amount).compareTo(card.getDailyLimit()) > 0) {
            return false;
        }
        if (card.getMonthlyLimit() != null
                && card.getCurrentMonthlySpent().add(amount).compareTo(card.getMonthlyLimit()) > 0) {
            return false;
        }
        return true;
    }

    /**
     * Track spending against limits
     */
    @Transactional
    public void recordCardSpending(Card card, BigDecimal amount) {
        card.setCurrentDailySpent(card.getCurrentDailySpent().add(amount));
        card.setCurrentMonthlySpent(card.getCurrentMonthlySpent().add(amount));
        repository.save(card);
    }

    /**
     * Basic Luhn algorithm for card validation
     */
    private boolean isValidCardNumber(String cardNumber) {
        if (!cardNumber.matches("\\d+") || cardNumber.length() < 13 || cardNumber.length() > 19) {
            return false;
        }
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10) == 0;
    }

    /**
     * Convert Card entity to DTO for API responses (masks card data)
     */
    private CardDTO convertToDTO(Card card) {
        return CardDTO.builder()
                .id(card.getId())
                .last4Digits(card.getLast4Digits())
                .cardholderName(card.getCardholderName())
                .expiryDate(card.getExpiryDate().toString())
                .cardNetwork(card.getCardNetwork())
                .cardType(card.getCardType().toString())
                .status(card.getStatus().toString())
                .isDefault(card.getIsDefault())
                .build();
    }
}
