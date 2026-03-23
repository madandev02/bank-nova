package com.banknova.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banknova.dto.SocialAuthRequest;
import com.banknova.dto.SocialAuthResponse;
import com.banknova.entity.User;
import com.banknova.repository.UserRepository;
import com.banknova.security.JwtUtil;

@Service
public class SocialAuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public SocialAuthResponse googleAuth(SocialAuthRequest request) {
        // Verify Google token here using Google's API
        // For now, we'll trust the token from frontend
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        boolean isNewUser = false;
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            isNewUser = true;
            user = new User();
            user.setEmail(request.getEmail());
            user.setName(request.getName());
            user.setPassword(""); // Social auth users don't have passwords
            user.setProvider("google");
            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new SocialAuthResponse(
                token,
                user.getId().toString(),
                user.getEmail(),
                user.getName(),
                "google",
                isNewUser);
    }

    public SocialAuthResponse appleAuth(SocialAuthRequest request) {
        // Verify Apple token here
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        boolean isNewUser = false;
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            isNewUser = true;
            user = new User();
            user.setEmail(request.getEmail());
            user.setName(request.getName());
            user.setPassword("");
            user.setProvider("apple");
            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new SocialAuthResponse(
                token,
                user.getId().toString(),
                user.getEmail(),
                user.getName(),
                "apple",
                isNewUser);
    }

    public SocialAuthResponse phoneAuth(SocialAuthRequest request) {
        // In production, verify phone number via Twilio OTP
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        boolean isNewUser = false;
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            isNewUser = true;
            user = new User();
            user.setEmail(request.getEmail());
            user.setName(request.getName() != null ? request.getName() : "User");
            user.setPassword("");
            user.setProvider("phone");
            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new SocialAuthResponse(
                token,
                user.getId().toString(),
                user.getEmail(),
                user.getName(),
                "phone",
                isNewUser);
    }
}
