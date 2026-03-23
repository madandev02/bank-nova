package com.banknova.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferVerificationDTO {
    private Long id;
    private String status;
    private String verificationMethod;
    private String message; // User-facing message
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class InitiateTransferVerificationRequest {
    private Long transferId;
    private String transferDetails;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class VerifyTransferRequest {
    private String otpCode;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ResendOtpRequest {
    private Long transferId;
}
