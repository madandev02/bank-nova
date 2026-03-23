package com.banknova.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banknova.entity.TransferVerification;
import com.banknova.entity.User;

@Repository
public interface TransferVerificationRepository extends JpaRepository<TransferVerification, Long> {
    Optional<TransferVerification> findByUserAndVerificationCode(User user, String verificationCode);

    Optional<TransferVerification> findByTransferId(Long transferId);
}
