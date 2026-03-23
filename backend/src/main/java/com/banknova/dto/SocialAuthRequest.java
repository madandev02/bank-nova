package com.banknova.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialAuthRequest {
    @Schema(description = "OAuth provider (google, apple, phone)", example = "google")
    private String provider;

    @Schema(description = "OAuth token or access code from provider", example = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjJjNmZhNmY1...")
    private String token;

    @Schema(description = "User's full name from social profile", example = "John Doe")
    private String name;

    @Schema(description = "User's email from social profile", example = "john@example.com")
    private String email;

    @Schema(description = "User's phone number (for phone auth)", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "Verification code (for phone OTP)", example = "123456")
    private String verificationCode;
}
