package com.banknova.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard API Response wrapper for all endpoints
 * Ensures consistent response format across the application
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {

    @Schema(description = "Response status (success/error)", example = "success")
    private String status;

    @Schema(description = "HTTP status code", example = "200")
    private Integer code;

    @Schema(description = "Response message", example = "Operation successful")
    private String message;

    @Schema(description = "Response data payload")
    private T data;

    @Schema(description = "Error details (if error)", example = "null")
    private ErrorDetails error;

    @Schema(description = "Timestamp of response generation")
    private LocalDateTime timestamp;

    @Schema(description = "Request tracking ID for debugging")
    private String requestId;

    // Success response constructors
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status("success")
                .code(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation successful");
    }

    public static ApiResponse<String> success(String message) {
        return ApiResponse.<String>builder()
                .status("success")
                .code(200)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Error response constructors
    public static ApiResponse<?> error(Integer code, String message, String errorCode) {
        return ApiResponse.builder()
                .status("error")
                .code(code)
                .message(message)
                .error(new ErrorDetails(errorCode, message))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetails {
        private String errorCode;
        private String errorMessage;
    }
}
