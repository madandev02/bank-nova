package com.banknova.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banknova.dto.AuthResponse;
import com.banknova.dto.LoginRequest;
import com.banknova.dto.RegisterRequest;
import com.banknova.entity.User;
import com.banknova.entity.Wallet;
import com.banknova.exception.AuthenticationException;
import com.banknova.exception.DuplicateEmailException;
import com.banknova.exception.UserNotFoundException;
import com.banknova.repository.UserRepository;
import com.banknova.repository.WalletRepository;
import com.banknova.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private EmailService emailService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user = userRepository.save(user);

        // Create wallet for user
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(java.math.BigDecimal.ZERO);
        walletRepository.save(wallet);

        String token = jwtUtil.generateToken(user.getEmail());

        // Send welcome email (if email service is available)
        if (emailService != null) {
            emailService.sendWelcomeEmail(user.getEmail(), user.getName());
        }

        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new AuthenticationException("Invalid email or password");
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    java.util.Collections.emptyList());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtil.generateToken(user.getEmail());

            return new AuthResponse(token, user.getId(), user.getName(), user.getEmail());
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new AuthenticationException("Invalid email or password");
        }
    }
}
