package com.banknova.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WalletDto {

    private Long id;
    private Long userId;
    private BigDecimal balance;
}
