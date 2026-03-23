package com.banknova.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banknova.dto.SendMoneyRequest;
import com.banknova.dto.TransactionDto;
import com.banknova.entity.Transaction;
import com.banknova.entity.TransactionStatus;
import com.banknova.entity.User;
import com.banknova.entity.Wallet;
import com.banknova.exception.InsufficientBalanceException;
import com.banknova.exception.InvalidTransactionException;
import com.banknova.exception.UserNotFoundException;
import com.banknova.repository.TransactionRepository;
import com.banknova.repository.UserRepository;
import com.banknova.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private EmailService emailService;

    @Transactional
    public TransactionDto sendMoney(String senderEmail, SendMoneyRequest request) {
        logger.info("Processing money transfer request from {} to {} for amount {}",
                senderEmail, request.getReceiverEmail(), request.getAmount());

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new UserNotFoundException(senderEmail));

        User receiver = userRepository.findByEmail(request.getReceiverEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getReceiverEmail()));

        if (sender.getId().equals(receiver.getId())) {
            logger.warn("Attempted self-transfer blocked for user: {}", senderEmail);
            throw new InvalidTransactionException("Cannot send money to yourself");
        }

        Wallet senderWallet = walletRepository.findByUserId(sender.getId())
                .orElseThrow(() -> new UserNotFoundException(sender.getId()));

        Wallet receiverWallet = walletRepository.findByUserId(receiver.getId())
                .orElseThrow(() -> new UserNotFoundException(receiver.getId()));

        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
            logger.warn("Insufficient balance for transfer. User: {}, Available: {}, Requested: {}",
                    senderEmail, senderWallet.getBalance(), request.getAmount());
            throw new InsufficientBalanceException(
                    String.format("Insufficient balance. Available: $%.2f, Required: $%.2f",
                            senderWallet.getBalance(), request.getAmount()));
        }

        // Update balances
        senderWallet.setBalance(senderWallet.getBalance().subtract(request.getAmount()));
        receiverWallet.setBalance(receiverWallet.getBalance().add(request.getAmount()));

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(request.getAmount());
        transaction.setStatus(TransactionStatus.SUCCESS);

        transaction = transactionRepository.save(transaction);

        auditLogger.info("MONEY_TRANSFER|sender={},receiver={},amount={},transactionId={}",
                senderEmail, request.getReceiverEmail(), request.getAmount(), transaction.getId());

        logger.info("Money transfer completed successfully. Transaction ID: {}", transaction.getId());

        // Send email notifications (if email service is available)
        if (emailService != null) {
            emailService.sendTransactionNotification(senderEmail, "SENT",
                    request.getAmount().toString(), request.getReceiverEmail());
            emailService.sendTransactionNotification(request.getReceiverEmail(), "RECEIVED",
                    request.getAmount().toString(), senderEmail);
        }

        return new TransactionDto(
                transaction.getId(),
                sender.getEmail(),
                receiver.getEmail(),
                transaction.getAmount(),
                transaction.getTimestamp(),
                transaction.getStatus().toString());
    }

    public List<TransactionDto> getTransactionHistory(String email) {
        logger.debug("Fetching transaction history for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        List<Transaction> transactions = transactionRepository
                .findBySenderIdOrReceiverIdOrderByTimestampDesc(user.getId(), user.getId());

        logger.debug("Found {} transactions for user: {}", transactions.size(), email);

        return transactions.stream()
                .map(t -> new TransactionDto(
                        t.getId(),
                        t.getSender().getEmail(),
                        t.getReceiver().getEmail(),
                        t.getAmount(),
                        t.getTimestamp(),
                        t.getStatus().toString()))
                .collect(Collectors.toList());
    }

    public Page<TransactionDto> getTransactionHistoryPaginated(String email, Pageable pageable) {
        logger.debug("Fetching paginated transaction history for user: {} (page: {}, size: {})",
                email, pageable.getPageNumber(), pageable.getPageSize());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        Page<Transaction> transactionPage = transactionRepository
                .findBySenderIdOrReceiverIdOrderByTimestampDesc(user.getId(), user.getId(), pageable);

        logger.debug("Found {} transactions (total: {}) for user: {}",
                transactionPage.getNumberOfElements(), transactionPage.getTotalElements(), email);

        return transactionPage.map(t -> new TransactionDto(
                t.getId(),
                t.getSender().getEmail(),
                t.getReceiver().getEmail(),
                t.getAmount(),
                t.getTimestamp(),
                t.getStatus().toString()));
    }
}
