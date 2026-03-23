package com.banknova.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.banknova.dto.AnalyticsDto;
import com.banknova.entity.Transaction;
import com.banknova.entity.User;
import com.banknova.entity.Wallet;
import com.banknova.exception.UserNotFoundException;
import com.banknova.repository.TransactionRepository;
import com.banknova.repository.UserRepository;
import com.banknova.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public AnalyticsDto getUserAnalytics(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new UserNotFoundException(user.getId()));

        List<Transaction> allTransactions = transactionRepository
                .findBySenderIdOrReceiverIdOrderByTimestampDesc(user.getId(), user.getId());

        // Calculate totals
        BigDecimal totalSent = allTransactions.stream()
                .filter(t -> t.getSender().getId().equals(user.getId()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReceived = allTransactions.stream()
                .filter(t -> t.getReceiver().getId().equals(user.getId()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate this month's data
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
                .atTime(LocalTime.MAX);

        List<Transaction> thisMonthTransactions = allTransactions.stream()
                .filter(t -> t.getTimestamp().isAfter(startOfMonth) && t.getTimestamp().isBefore(endOfMonth))
                .toList();

        BigDecimal sentThisMonth = thisMonthTransactions.stream()
                .filter(t -> t.getSender().getId().equals(user.getId()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal receivedThisMonth = thisMonthTransactions.stream()
                .filter(t -> t.getReceiver().getId().equals(user.getId()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate lastTransactionDate = allTransactions.stream()
                .findFirst()
                .map(t -> t.getTimestamp().toLocalDate())
                .orElse(null);

        return new AnalyticsDto(
                wallet.getBalance(),
                (long) allTransactions.size(),
                totalSent,
                totalReceived,
                (long) thisMonthTransactions.size(),
                sentThisMonth,
                receivedThisMonth,
                lastTransactionDate);
    }
}
