package com.banknova.service;

import com.banknova.dto.WalletDto;
import com.banknova.entity.User;
import com.banknova.entity.Wallet;
import com.banknova.repository.UserRepository;
import com.banknova.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletDto getWalletBalance(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return new WalletDto(wallet.getId(), user.getId(), wallet.getBalance());
    }
}
