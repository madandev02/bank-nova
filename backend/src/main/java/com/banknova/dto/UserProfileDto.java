package com.banknova.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User profile information")
public class UserProfileDto {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "Full name", example = "John Doe")
    private String name;

    @Schema(description = "Email address", example = "john@example.com")
    private String email;

    @Schema(description = "Phone number", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "Profile picture URL")
    private String profilePictureUrl;

    @Schema(description = "Date of birth", example = "1990-01-15")
    private String dateOfBirth;

    @Schema(description = "Country", example = "United States")
    private String country;

    @Schema(description = "City", example = "New York")
    private String city;

    @Schema(description = "Address", example = "123 Main Street")
    private String address;

    @Schema(description = "KYC verification status", example = "VERIFIED")
    private String kycStatus; // PENDING, VERIFIED, REJECTED

    @Schema(description = "Account creation date")
    private LocalDateTime createdAt;

    @Schema(description = "Last login date")
    private LocalDateTime lastLogin;

    @Schema(description = "2FA enabled status")
    private Boolean twoFactorEnabled;

    @Schema(description = "Email verified status")
    private Boolean emailVerified;

    @Schema(description = "Phone verified status")
    private Boolean phoneVerified;

    @Schema(description = "Account status", example = "ACTIVE")
    private String accountStatus; // ACTIVE, SUSPENDED, CLOSED
}
