package com.banknova.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User sender;
    private User receiver;
    private Wallet senderWallet;
    private Wallet receiverWallet;
    private SendMoneyRequest request;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1L);
        sender.setEmail("sender@example.com");

        receiver = new User();
        receiver.setId(2L);
        receiver.setEmail("receiver@example.com");

        senderWallet = new Wallet();
        senderWallet.setId(1L);
        senderWallet.setUser(sender);
        senderWallet.setBalance(new BigDecimal("1000.00"));

        receiverWallet = new Wallet();
        receiverWallet.setId(2L);
        receiverWallet.setUser(receiver);
        receiverWallet.setBalance(new BigDecimal("500.00"));

        request = new SendMoneyRequest();
        request.setReceiverEmail("receiver@example.com");
        request.setAmount(new BigDecimal("100.00"));
    }

    @Test
    void sendMoney_ShouldTransferSuccessfully_WhenValidRequest() {
        // Arrange
        when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(userRepository.findByEmail("receiver@example.com")).thenReturn(Optional.of(receiver));
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByUserId(2L)).thenReturn(Optional.of(receiverWallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        // Act
        TransactionDto result = transactionService.sendMoney("sender@example.com", request);

        // Assert
        assertNotNull(result);
        assertEquals("sender@example.com", result.getSenderEmail());
        assertEquals("receiver@example.com", result.getReceiverEmail());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        assertEquals("SUCCESS", result.getStatus());

        verify(walletRepository).save(senderWallet);
        verify(walletRepository).save(receiverWallet);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void sendMoney_ShouldThrowInsufficientBalanceException_WhenBalanceTooLow() {
        // Arrange
        senderWallet.setBalance(new BigDecimal("50.00")); // Less than required 100.00
        when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(userRepository.findByEmail("receiver@example.com")).thenReturn(Optional.of(receiver));
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByUserId(2L)).thenReturn(Optional.of(receiverWallet));

        // Act & Assert
        assertThrows(InsufficientBalanceException.class,
                () -> transactionService.sendMoney("sender@example.com", request));
    }

    @Test
    void sendMoney_ShouldThrowInvalidTransactionException_WhenSendingToSelf() {
        // Arrange
        request.setReceiverEmail("sender@example.com"); // Same as sender
        when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));

        // Act & Assert
        assertThrows(InvalidTransactionException.class,
                () -> transactionService.sendMoney("sender@example.com", request));
    }

    @Test
    void sendMoney_ShouldThrowUserNotFoundException_WhenReceiverNotFound() {
        // Arrange
        when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(userRepository.findByEmail("receiver@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> transactionService.sendMoney("sender@example.com", request));
    }

    @Test
    void getTransactionHistoryPaginated_ShouldReturnPagedResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Transaction> transactions = Arrays.asList(createTransaction());
        Page<Transaction> transactionPage = new PageImpl<>(transactions, pageable, 1);

        when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(transactionRepository.findBySenderIdOrReceiverIdOrderByTimestampDesc(eq(1L), eq(1L), any(Pageable.class)))
                .thenReturn(transactionPage);

        // Act
        Page<TransactionDto> result = transactionService.getTransactionHistoryPaginated("sender@example.com", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }

    private Transaction createTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setStatus(TransactionStatus.SUCCESS);
        return transaction;
    }
}
