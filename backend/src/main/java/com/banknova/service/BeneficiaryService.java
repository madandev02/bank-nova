package com.banknova.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banknova.entity.Beneficiary;
import com.banknova.entity.User;
import com.banknova.repository.BeneficiaryRepository;
import com.banknova.exception.BankNovaException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BeneficiaryService {

    private final BeneficiaryRepository repository;
    private final EmailService emailService;

    /**
     * Add a new beneficiary (requires verification via email)
     */
    @Transactional
    public Beneficiary addBeneficiary(
            User user,
            String beneficiaryName,
            String beneficiaryEmail,
            String accountNumber,
            String relationship) {
        // Check if beneficiary already exists
        if (repository.findByUserAndBeneficiaryEmail(user, beneficiaryEmail).isPresent()) {
            throw new BankNovaException("This beneficiary has already been added");
        }

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setUser(user);
        beneficiary.setBeneficiaryName(beneficiaryName);
        beneficiary.setBeneficiaryEmail(beneficiaryEmail);
        beneficiary.setAccountNumber(accountNumber);
        beneficiary.setRelationship(relationship);
        beneficiary.setIsVerified(false);
        beneficiary.setVerificationToken(UUID.randomUUID().toString());

        Beneficiary saved = repository.save(beneficiary);

        // Send verification email
        emailService.sendBeneficiaryVerificationEmail(
                beneficiaryEmail,
                beneficiary.getVerificationToken(),
                beneficiaryName);

        return saved;
    }

    /**
     * Verify beneficiary via email link
     */
    @Transactional
    public void verifyBeneficiary(String token) {
        Beneficiary beneficiary = repository.findByVerificationToken(token)
                .orElseThrow(() -> new BankNovaException("Invalid verification token"));

        beneficiary.setIsVerified(true);
        beneficiary.setVerificationDate(java.time.LocalDateTime.now());
        repository.save(beneficiary);
    }

    /**
     * Get all verified beneficiaries for a user
     */
    public List<Beneficiary> getVerifiedBeneficiaries(User user) {
        return repository.findByUserAndIsVerified(user, true);
    }

    /**
     * Get all beneficiaries for a user
     */
    public List<Beneficiary> getAllBeneficiaries(User user) {
        return repository.findByUser(user);
    }

    /**
     * Remove a beneficiary
     */
    @Transactional
    public void removeBeneficiary(User user, Long beneficiaryId) {
        Beneficiary beneficiary = repository.findById(beneficiaryId)
                .orElseThrow(() -> new BankNovaException("Beneficiary not found"));

        if (!beneficiary.getUser().getId().equals(user.getId())) {
            throw new BankNovaException("Unauthorized: This beneficiary does not belong to you");
        }

        repository.delete(beneficiary);
    }

    /**
     * Check if a recipient is a verified beneficiary for faster transfer
     */
    public boolean isTrustedBeneficiary(User user, String recipientEmail) {
        return repository.findByUserAndBeneficiaryEmail(user, recipientEmail)
                .map(Beneficiary::getIsVerified)
                .orElse(false);
    }
}
