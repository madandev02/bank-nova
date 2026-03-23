package com.banknova.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banknova.dto.BeneficiaryDTO;
import com.banknova.entity.User;
import com.banknova.exception.BankNovaException;
import com.banknova.repository.UserRepository;
import com.banknova.service.BeneficiaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/beneficiaries")
@RequiredArgsConstructor
@Tag(name = "Beneficiary Management", description = "Manage trusted recipients for transfers")
@SecurityRequirement(name = "Bearer Authentication")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;
    private final UserRepository userRepository;

    @PostMapping("/add")
    @Operation(summary = "Add a beneficiary", description = "Add a new trusted recipient (requires email verification)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Beneficiary added, verification email sent"),
            @ApiResponse(responseCode = "400", description = "Invalid beneficiary details"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "409", description = "Beneficiary already exists")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<BeneficiaryDTO>> addBeneficiary(
            Authentication authentication,
            @Valid @RequestBody AddBeneficiaryRequest request) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        var beneficiary = beneficiaryService.addBeneficiary(
                user,
                request.getBeneficiaryName(),
                request.getBeneficiaryEmail(),
                request.getAccountNumber(),
                request.getRelationship());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.banknova.dto.ApiResponse.success(convertToDTO(beneficiary),
                        "Beneficiary added. Verification email has been sent."));
    }

    @GetMapping
    @Operation(summary = "Get beneficiaries", description = "Retrieve all beneficiaries for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Beneficiaries retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<List<BeneficiaryDTO>>> getBeneficiaries(
            Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        List<BeneficiaryDTO> beneficiaries = beneficiaryService.getAllBeneficiaries(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(com.banknova.dto.ApiResponse.success(beneficiaries, "Beneficiaries retrieved successfully"));
    }

    @GetMapping("/verified")
    @Operation(summary = "Get verified beneficiaries", description = "Retrieve only verified trusted recipients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verified beneficiaries retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<List<BeneficiaryDTO>>> getVerifiedBeneficiaries(
            Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        List<BeneficiaryDTO> beneficiaries = beneficiaryService.getVerifiedBeneficiaries(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                com.banknova.dto.ApiResponse.success(beneficiaries, "Verified beneficiaries retrieved successfully"));
    }

    @DeleteMapping("/{beneficiaryId}")
    @Operation(summary = "Remove beneficiary", description = "Remove a beneficiary from your trusted list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Beneficiary removed successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Beneficiary not found")
    })
    public ResponseEntity<com.banknova.dto.ApiResponse<String>> removeBeneficiary(
            Authentication authentication,
            @PathVariable Long beneficiaryId) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BankNovaException("User not found"));

        beneficiaryService.removeBeneficiary(user, beneficiaryId);
        return ResponseEntity.ok(com.banknova.dto.ApiResponse.success("Beneficiary removed successfully"));
    }

    private BeneficiaryDTO convertToDTO(com.banknova.entity.Beneficiary beneficiary) {
        return BeneficiaryDTO.builder()
                .id(beneficiary.getId())
                .beneficiaryName(beneficiary.getBeneficiaryName())
                .beneficiaryEmail(beneficiary.getBeneficiaryEmail())
                .accountNumber(beneficiary.getAccountNumber())
                .relationship(beneficiary.getRelationship())
                .isVerified(beneficiary.getIsVerified())
                .verificationDate(beneficiary.getVerificationDate())
                .createdAt(beneficiary.getCreatedAt())
                .build();
    }
}

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class AddBeneficiaryRequest {
    private String beneficiaryName;
    private String beneficiaryEmail;
    private String accountNumber;
    private String relationship;
}
