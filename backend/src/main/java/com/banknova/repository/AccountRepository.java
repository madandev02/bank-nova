package com.banknova.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banknova.entity.Account;
import com.banknova.entity.User;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByUserId(Long userId);

    Optional<Account> findByIdAndUserId(Long accountId, Long userId);

    Optional<Account> findByAccountNumberAndUserId(String accountNumber, Long userId);

    Optional<Account> findByUserAndAccountType(User user, String accountType);
}
