package com.banknova.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Update user profile request")
public class UpdateProfileRequest {

    @Schema(description = "Full name", example = "John Doe")
    private String name;

    @Schema(description = "Phone number", example = "+1234567890")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Schema(description = "Date of birth", example = "1990-01-15")
    private String dateOfBirth;

    @Schema(description = "Country", example = "United States")
    private String country;

    @Schema(description = "City", example = "New York")
    private String city;

    @Schema(description = "Residential address", example = "123 Main Street")
    private String address;

    @Schema(description = "Profile picture URL")
    private String profilePictureUrl;
}
