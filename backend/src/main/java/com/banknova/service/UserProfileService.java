package com.banknova.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banknova.dto.UpdateProfileRequest;
import com.banknova.dto.UserProfileDto;
import com.banknova.entity.User;
import com.banknova.exception.UserNotFoundException;
import com.banknova.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    private final UserRepository userRepository;

    public UserProfileDto getUserProfile(String email) {
        logger.debug("Fetching profile for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return mapToProfileDto(user);
    }

    @Transactional
    public UserProfileDto updateUserProfile(String email, UpdateProfileRequest request) {
        logger.info("Updating profile for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        // Update fields if provided
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            user.setPhoneNumber(request.getPhoneNumber());
            user.setPhoneVerified(false); // Reset verification when phone changes
        }

        if (request.getDateOfBirth() != null && !request.getDateOfBirth().isBlank()) {
            user.setDateOfBirth(request.getDateOfBirth());
        }

        if (request.getCountry() != null && !request.getCountry().isBlank()) {
            user.setCountry(request.getCountry());
        }

        if (request.getCity() != null && !request.getCity().isBlank()) {
            user.setCity(request.getCity());
        }

        if (request.getAddress() != null && !request.getAddress().isBlank()) {
            user.setAddress(request.getAddress());
        }

        if (request.getProfilePictureUrl() != null && !request.getProfilePictureUrl().isBlank()) {
            user.setProfilePictureUrl(request.getProfilePictureUrl());
        }

        user = userRepository.save(user);

        logger.info("Profile updated successfully for user: {}", email);

        return mapToProfileDto(user);
    }

    public UserProfileDto getUserProfileById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return mapToProfileDto(user);
    }

    @Transactional
    public void recordLoginActivity(String email, String ipAddress) {
        logger.debug("Recording login activity for user: {} from IP: {}", email, ipAddress);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        user.setLastLogin(LocalDateTime.now());
        user.setLastKnownIp(ipAddress);
        user.setLoginAttempts(0); // Reset failed login attempts on successful login

        userRepository.save(user);
    }

    private UserProfileDto mapToProfileDto(User user) {
        UserProfileDto dto = new UserProfileDto();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setCountry(user.getCountry());
        dto.setCity(user.getCity());
        dto.setAddress(user.getAddress());
        dto.setKycStatus(user.getKycStatus());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLogin(user.getLastLogin());
        dto.setTwoFactorEnabled(user.getTwoFactorEnabled());
        dto.setEmailVerified(user.getEmailVerified());
        dto.setPhoneVerified(user.getPhoneVerified());
        dto.setAccountStatus(user.getAccountStatus());

        return dto;
    }
}
