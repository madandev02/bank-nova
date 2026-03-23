package com.banknova.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialAuthResponse {
    @Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "User ID", example = "507f1f77bcf86cd799439011")
    private String id;

    @Schema(description = "User's email", example = "john@example.com")
    private String email;

    @Schema(description = "User's name", example = "John Doe")
    private String name;

    @Schema(description = "Authentication provider", example = "google")
    private String provider;

    @Schema(description = "Whether this is a new account", example = "false")
    private boolean isNewUser;
}
