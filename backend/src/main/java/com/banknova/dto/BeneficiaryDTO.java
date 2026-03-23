package com.banknova.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryDTO {
    private Long id;
    private String beneficiaryName;
    private String beneficiaryEmail;
    private String accountNumber;
    private String relationship;
    private Boolean isVerified;
    private LocalDateTime verificationDate;
    private LocalDateTime createdAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AddBeneficiaryRequest {
    private String beneficiaryName;
    private String beneficiaryEmail;
    private String accountNumber;
    private String relationship; // FRIEND, FAMILY, WORK, OTHER
}
