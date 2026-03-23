package com.banknova.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "login_activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String ipAddress;

    @Column
    private String deviceInfo; // User-Agent or device identifier

    @Column
    private String country;

    @Column
    private String city;

    @Column(nullable = false)
    private String loginStatus; // SUCCESS, FAILED, 2FA_PENDING

    @Column
    private String failureReason;

    @CreationTimestamp
    private LocalDateTime loginTime;

    @Column
    private LocalDateTime logoutTime;

    @Column
    private Long sessionDurationSeconds;

    @Column
    private String deviceFingerprint;
}
