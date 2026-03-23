package com.banknova.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.banknova.entity.Loan;
import com.banknova.entity.Loan.LoanStatus;
import com.banknova.entity.User;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUser(User user);

    List<Loan> findByUserAndStatus(User user, LoanStatus status);

    Optional<Loan> findByLoanNumber(String loanNumber);

    @Query("SELECT l FROM Loan l WHERE l.user = :user AND l.status IN ('ACTIVE', 'APPROVED')")
    List<Loan> findActiveLoans(@Param("user") User user);
}
