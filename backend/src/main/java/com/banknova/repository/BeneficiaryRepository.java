package com.banknova.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banknova.entity.Beneficiary;
import com.banknova.entity.User;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    List<Beneficiary> findByUser(User user);

    List<Beneficiary> findByUserAndIsVerified(User user, Boolean isVerified);

    Optional<Beneficiary> findByUserAndBeneficiaryEmail(User user, String beneficiaryEmail);

    Optional<Beneficiary> findByVerificationToken(String token);
}
