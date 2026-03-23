package com.banknova.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banknova.dto.SocialAuthRequest;
import com.banknova.dto.SocialAuthResponse;
import com.banknova.service.SocialAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/auth/social")
@Tag(name = "Social Authentication", description = "OAuth and social login endpoints")
public class SocialAuthController {
    @Autowired
    private SocialAuthService socialAuthService;

    @PostMapping("/google")
    @Operation(summary = "Google OAuth login/signup")
    public ResponseEntity<SocialAuthResponse> googleAuth(@RequestBody SocialAuthRequest request) {
        return ResponseEntity.ok(socialAuthService.googleAuth(request));
    }

    @PostMapping("/apple")
    @Operation(summary = "Apple Sign-In login/signup")
    public ResponseEntity<SocialAuthResponse> appleAuth(@RequestBody SocialAuthRequest request) {
        return ResponseEntity.ok(socialAuthService.appleAuth(request));
    }

    @PostMapping("/phone")
    @Operation(summary = "Phone number authentication")
    public ResponseEntity<SocialAuthResponse> phoneAuth(@RequestBody SocialAuthRequest request) {
        return ResponseEntity.ok(socialAuthService.phoneAuth(request));
    }
}
