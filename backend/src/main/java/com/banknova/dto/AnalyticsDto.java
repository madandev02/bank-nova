package com.banknova.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User financial analytics and metrics")
public class AnalyticsDto {
    @Schema(description = "Current account balance", example = "1250.75")
    private BigDecimal totalBalance;
    @Schema(description = "Total number of transactions", example = "45")
    private Long totalTransactions;
    @Schema(description = "Total amount sent", example = "2300.50")
    private BigDecimal totalSent;
    @Schema(description = "Total amount received", example = "3551.25")
    private BigDecimal totalReceived;
    @Schema(description = "Number of transactions this month", example = "12")
    private Long transactionsThisMonth;
    @Schema(description = "Amount sent this month", example = "450.00")
    private BigDecimal sentThisMonth;
    @Schema(description = "Amount received this month", example = "600.25")
    private BigDecimal receivedThisMonth;
    @Schema(description = "Date of last transaction", example = "2024-01-15")
    private LocalDate lastTransactionDate;
}
