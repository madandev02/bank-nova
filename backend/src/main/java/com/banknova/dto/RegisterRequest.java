package com.banknova.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "User registration request payload")
public class RegisterRequest {

    @Schema(description = "User full name", example = "John Doe", required = true)
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "User email address", example = "john.doe@example.com", required = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(description = "User password (minimum 6 characters)", example = "password123", required = true, minLength = 6)
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password should be at least 6 characters")
    private String password;
}
