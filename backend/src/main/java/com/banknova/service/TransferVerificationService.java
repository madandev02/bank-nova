package com.banknova.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banknova.entity.TransferVerification;
import com.banknova.entity.TransferVerification.VerificationStatus;
import com.banknova.entity.User;
import com.banknova.repository.TransferVerificationRepository;
import com.banknova.exception.BankNovaException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferVerificationService {

    private final TransferVerificationRepository repository;
    private final EmailService emailService;
    private final Random random = new Random();

    /**
     * Generate OTP and send via email for transfer verification.
     * OTP is valid for 10 minutes.
     */
    @Transactional
    public TransferVerification generateTransferOtp(User user, Long transferId, String transferDetails) {
        // Invalidate any previous verification for this transfer
        repository.findByTransferId(transferId).ifPresent(existing -> {
            existing.setStatus(VerificationStatus.CANCELLED);
            repository.save(existing);
        });

        String otp = generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10);

        TransferVerification verification = new TransferVerification();
        verification.setUser(user);
        verification.setVerificationCode(otp);
        verification.setTransferId(transferId);
        verification.setTransferDetails(transferDetails);
        verification.setStatus(VerificationStatus.PENDING);
        verification.setVerificationMethod("EMAIL");
        verification.setExpiryTime(expiryTime);
        verification.setAttemptCount(0);

        TransferVerification saved = repository.save(verification);

        // Send OTP via email
        emailService.sendTransferVerificationCode(user.getEmail(), otp, user.getName());

        return saved;
    }

    /**
     * Verify the OTP code provided by user.
     * Prevents brute force with attempt limiting.
     */
    @Transactional
    public boolean verifyTransferOtp(User user, String otpCode) {
        TransferVerification verification = repository.findByUserAndVerificationCode(user, otpCode)
                .orElseThrow(() -> new BankNovaException("Invalid verification code"));

        if (verification.isExpired()) {
            verification.setStatus(VerificationStatus.EXPIRED);
            repository.save(verification);
            throw new BankNovaException("Verification code has expired");
        }

        if (verification.getStatus() != VerificationStatus.PENDING) {
            throw new BankNovaException("Verification code is not valid");
        }

        // Track attempts to prevent brute force
        if (verification.getAttemptCount() >= 5) {
            verification.setStatus(VerificationStatus.CANCELLED);
            repository.save(verification);
            throw new BankNovaException("Too many failed attempts. Please request a new verification code.");
        }

        verification.setStatus(VerificationStatus.VERIFIED);
        verification.setVerifiedAt(LocalDateTime.now());
        repository.save(verification);

        return true;
    }

    /**
     * Resend OTP for a pending transfer
     */
    @Transactional
    public void resendOtp(User user, Long transferId) {
        TransferVerification verification = repository.findByTransferId(transferId)
                .orElseThrow(() -> new BankNovaException("No verification found for this transfer"));

        if (!verification.getStatus().equals(VerificationStatus.PENDING)) {
            throw new BankNovaException("Cannot resend OTP for this transfer");
        }

        String newOtp = generateOtp();
        verification.setVerificationCode(newOtp);
        verification.setExpiryTime(LocalDateTime.now().plusMinutes(10));
        verification.setAttemptCount(0);
        repository.save(verification);

        emailService.sendTransferVerificationCode(user.getEmail(), newOtp, user.getName());
    }

    /**
     * Increment failed attempt count
     */
    @Transactional
    public void recordFailedAttempt(User user, String otpCode) {
        repository.findByUserAndVerificationCode(user, otpCode).ifPresent(verification -> {
            verification.setAttemptCount(verification.getAttemptCount() + 1);
            verification.setLastAttemptTime(LocalDateTime.now());
            repository.save(verification);
        });
    }

    private String generateOtp() {
        return String.format("%06d", random.nextInt(1000000));
    }
}
