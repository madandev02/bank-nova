package com.banknova.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banknova.entity.Account;
import com.banknova.entity.SpendingLimit;
import com.banknova.entity.SpendingLimit.LimitType;
import com.banknova.entity.SpendingLimit.TransactionCategory;
import com.banknova.entity.User;
import com.banknova.exception.BankNovaException;
import com.banknova.repository.SpendingLimitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpendingLimitService {

    private final SpendingLimitRepository repository;

    /**
     * Set spending limit for a category
     */
    @Transactional
    public SpendingLimit setSpendingLimit(
            User user,
            Account account,
            TransactionCategory category,
            LimitType limitType,
            BigDecimal limitAmount) {
        if (limitAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankNovaException("Limit amount must be greater than zero");
        }

        // Check if limit already exists for this category/type combination
        SpendingLimit existing = repository.findByUserAndCategoryAndLimitType(user, category, limitType)
                .orElse(null);

        SpendingLimit limit = existing != null ? existing : new SpendingLimit();
        limit.setUser(user);
        limit.setAccount(account);
        limit.setCategory(category);
        limit.setLimitType(limitType);
        limit.setLimitAmount(limitAmount);
        limit.setIsActive(true);

        if (limit.getResetDate() == null) {
            limit.updateResetDate();
        }

        return repository.save(limit);
    }

    /**
     * Check if transaction violates spending limit
     */
    public boolean isSpendingAllowed(User user, TransactionCategory category, BigDecimal amount) {
        List<SpendingLimit> limits = repository.findByUserAndIsActive(user, true);

        for (SpendingLimit limit : limits) {
            if (limit.getCategory().equals(category)) {
                limit.resetIfNeeded();
                if (limit.isLimitExceeded(amount)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Record spending against limit
     */
    @Transactional
    public void recordSpending(User user, TransactionCategory category, BigDecimal amount) {
        SpendingLimit limit = repository.findByUserAndCategoryAndLimitType(
                user,
                category,
                LimitType.MONTHLY // Default to monthly tracking
        ).orElse(null);

        if (limit != null) {
            limit.resetIfNeeded();
            limit.setCurrentSpent(limit.getCurrentSpent().add(amount));
            repository.save(limit);
        }
    }

    /**
     * Get all spending limits for user
     */
    public List<SpendingLimit> getUserLimits(User user) {
        return repository.findByUserAndIsActive(user, true);
    }

    /**
     * Get remaining budget for category
     */
    public BigDecimal getRemainingBudget(User user, TransactionCategory category) {
        SpendingLimit limit = repository.findByUserAndCategoryAndLimitType(
                user,
                category,
                LimitType.MONTHLY).orElse(null);

        if (limit == null) {
            return null; // No limit set
        }

        limit.resetIfNeeded();
        return limit.getLimitAmount().subtract(limit.getCurrentSpent());
    }

    /**
     * Disable a spending limit
     */
    @Transactional
    public void disableLimit(User user, Long limitId) {
        SpendingLimit limit = repository.findById(limitId)
                .orElseThrow(() -> new BankNovaException("Spending limit not found"));

        if (!limit.getUser().getId().equals(user.getId())) {
            throw new BankNovaException("Unauthorized: This limit does not belong to you");
        }

        limit.setIsActive(false);
        repository.save(limit);
    }
}
