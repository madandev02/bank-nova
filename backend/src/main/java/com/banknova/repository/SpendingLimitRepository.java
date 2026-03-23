package com.banknova.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banknova.entity.SpendingLimit;
import com.banknova.entity.SpendingLimit.LimitType;
import com.banknova.entity.SpendingLimit.TransactionCategory;
import com.banknova.entity.User;

@Repository
public interface SpendingLimitRepository extends JpaRepository<SpendingLimit, Long> {
    List<SpendingLimit> findByUser(User user);

    List<SpendingLimit> findByUserAndIsActive(User user, Boolean isActive);

    Optional<SpendingLimit> findByUserAndCategoryAndLimitType(
            User user,
            TransactionCategory category,
            LimitType limitType);
}
