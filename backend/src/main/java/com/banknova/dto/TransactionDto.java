package com.banknova.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Transaction information")
public class TransactionDto {

    @Schema(description = "Transaction ID", example = "1")
    private Long id;

    @Schema(description = "Sender's email address", example = "sender@example.com")
    private String senderEmail;

    @Schema(description = "Receiver's email address", example = "receiver@example.com")
    private String receiverEmail;

    @Schema(description = "Transaction amount", example = "100.50")
    private BigDecimal amount;

    @Schema(description = "Transaction timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Transaction status", example = "COMPLETED", allowableValues = { "PENDING", "COMPLETED",
            "FAILED" })
    private String status;
}
