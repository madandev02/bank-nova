package com.banknova.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banknova.entity.LoginActivity;

@Repository
public interface LoginActivityRepository extends JpaRepository<LoginActivity, Long> {
    Page<LoginActivity> findByUserIdOrderByLoginTimeDesc(Long userId, Pageable pageable);

    List<LoginActivity> findByUserIdAndLoginStatusAndLoginTimeAfter(
            Long userId, String loginStatus, LocalDateTime loginTime);

    List<LoginActivity> findByUserIdAndLoginStatus(Long userId, String loginStatus);
}
