package com.banknova.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banknova.dto.WalletDto;
import com.banknova.service.WalletService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    public ResponseEntity<WalletDto> getBalance(Authentication authentication) {
        String email = authentication.getName();
        WalletDto wallet = walletService.getWalletBalance(email);
        return ResponseEntity.ok(wallet);
    }
}
