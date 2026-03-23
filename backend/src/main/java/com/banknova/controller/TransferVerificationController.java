package com.banknova.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banknova.dto.TransferVerificationDTO;
import com.banknova.entity.User;
import com.banknova.exception.BankNovaException;
import com.banknova.repository.UserRepository;
import com.banknova.service.TransferVerificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/verify-transfer")
@RequiredArgsConstructor
@Tag(name = "Transfer Verification", description = "OTP-based transfer verification for enhanced security")
@SecurityRequirement(name = "Bearer Authentication")
public class TransferVerificationController {

    private final TransferVerificationService verificationService;
    private final UserRepository userRepository;

    @PostMapping("/initiate")
    @Operation(summary = "Initiate transfer verification", description = "Generate and send OTP for transfer verification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OTP generated and sent via email"),
            @ApiResponse(responseCode = "400", description = "Invalid transfer details"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "409", description = "Transfer already being verified")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<TransferVerificationDTO>> initiateTransferVerification(
            Authentication authentication,
            @Valid @RequestBody InitiateTransferVerificationRequest request) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        var verification = verificationService.generateTransferOtp(
                user,
                request.getTransferId(),
                request.getTransferDetails());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.banknova.dto.ApiResponse.success(
                        TransferVerificationDTO.builder()
                                .id(verification.getId())
                                .status(verification.getStatus().toString())
                                .verificationMethod(verification.getVerificationMethod())
                                .message("OTP sent to your registered email")
                                .build(),
                        "Verification initiated. Check your email for the verification code."));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify transfer with OTP", description = "Submit OTP code to verify and approve transfer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired OTP"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "429", description = "Too many failed attempts")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<String>> verifyTransferCode(
            Authentication authentication,
            @Valid @RequestBody VerifyTransferRequest request) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        try {
            verificationService.verifyTransferOtp(user, request.getOtpCode());
            return ResponseEntity.ok(com.banknova.dto.ApiResponse.success("Transfer verified successfully"));
        } catch (BankNovaException e) {
            verificationService.recordFailedAttempt(user, request.getOtpCode());
            throw e;
        }
    }

    @PostMapping("/resend")
    @Operation(summary = "Resend OTP", description = "Request a new OTP for transfer verification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP resent successfully"),
            @ApiResponse(responseCode = "400", description = "Cannot resend OTP"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Transfer verification not found")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<String>> resendOtp(
            Authentication authentication,
            @Valid @RequestBody ResendOtpRequest request) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        verificationService.resendOtp(user, request.getTransferId());
        return ResponseEntity.ok(com.banknova.dto.ApiResponse.success("New OTP sent to your email"));
    }

    @GetMapping("/status")
    @Operation(summary = "Check verification status", description = "Check the status of a pending transfer verification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification status retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Transfer not found")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<TransferVerificationDTO>> getVerificationStatus(
            @RequestParam Long transferId) {

        return ResponseEntity.ok(com.banknova.dto.ApiResponse.success(null, "Verification status retrieved"));
    }
}

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class InitiateTransferVerificationRequest {
    private Long transferId;
    private String transferDetails;
}

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class VerifyTransferRequest {
    private String otpCode;
}

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class ResendOtpRequest {
    private Long transferId;
}
