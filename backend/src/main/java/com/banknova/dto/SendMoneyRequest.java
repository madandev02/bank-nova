package com.banknova.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Money transfer request payload")
public class SendMoneyRequest {

    @Schema(description = "Receiver's email address", example = "receiver@example.com", required = true)
    @NotBlank(message = "Receiver email is required")
    @Email(message = "Receiver email should be valid")
    private String receiverEmail;

    @Schema(description = "Transfer amount (must be greater than 0)", example = "50.00", required = true, minimum = "0.01")
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
}
