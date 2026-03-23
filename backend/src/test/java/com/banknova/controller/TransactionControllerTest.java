package com.banknova.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import com.banknova.dto.SendMoneyRequest;
import com.banknova.dto.TransactionDto;
import com.banknova.exception.InsufficientBalanceException;
import com.banknova.security.JwtAuthenticationFilter;
import com.banknova.service.AnalyticsService;
import com.banknova.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private AnalyticsService analyticsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sendMoney_ValidRequest_ReturnsTransaction() throws Exception {
        // Given
        SendMoneyRequest request = new SendMoneyRequest();
        request.setReceiverEmail("receiver@example.com");
        request.setAmount(new BigDecimal("100.00"));

        TransactionDto response = new TransactionDto(
                1L,
                "sender@example.com",
                "receiver@example.com",
                new BigDecimal("100.00"),
                LocalDateTime.now(),
                "COMPLETED");

        when(transactionService.sendMoney(eq("sender@example.com"), any(SendMoneyRequest.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/transactions/send")
                .principal(new UsernamePasswordAuthenticationToken("sender@example.com", null))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.senderEmail").value("sender@example.com"))
                .andExpect(jsonPath("$.receiverEmail").value("receiver@example.com"))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void getTransactionHistory_ReturnsTransactions() throws Exception {
        // Given
        List<TransactionDto> transactions = Arrays.asList(
                new TransactionDto(1L, "user@example.com", "other@example.com",
                        new BigDecimal("50.00"), LocalDateTime.now(), "COMPLETED"),
                new TransactionDto(2L, "user@example.com", "another@example.com",
                        new BigDecimal("25.00"), LocalDateTime.now(), "COMPLETED"));

        when(transactionService.getTransactionHistory(eq("user@example.com")))
                .thenReturn(transactions);

        // When & Then
        mockMvc.perform(get("/api/v1/transactions/history")
                .principal(new UsernamePasswordAuthenticationToken("user@example.com", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].amount").value(50.00))
                .andExpect(jsonPath("$[1].amount").value(25.00));
    }

    @Test
    void sendMoney_InsufficientBalance_ReturnsBadRequest() throws Exception {
        // Given
        SendMoneyRequest request = new SendMoneyRequest();
        request.setReceiverEmail("receiver@example.com");
        request.setAmount(new BigDecimal("1000.00"));

        when(transactionService.sendMoney(eq("sender@example.com"), any(SendMoneyRequest.class)))
                .thenThrow(new InsufficientBalanceException("Insufficient balance"));

        // When & Then
        mockMvc.perform(post("/api/v1/transactions/send")
                .principal(new UsernamePasswordAuthenticationToken("sender@example.com", null))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
