package com.homeservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Dto {


    @Data
    public static class RegisterRequest {
        @NotBlank @Email
        public String email;
        @NotBlank
        public String fullname;
        @NotBlank
        public String address;
        @NotBlank
        public String phone;
        @NotBlank
        public String password;
        public String role = "customer"; // defaults to customer
    }


    @Data
    public static class LoginRequest {
        @NotBlank @Email
        public String email;
        @NotBlank
        public String password;
    }

    @Data
    @AllArgsConstructor
    public static class LoginResponse {
        public Long id;
        public String email;
        public String fullname;
        public String role;
        public String message;
    }

    @Data
    @AllArgsConstructor
    public static class UserResponse {
        public Long id;
        public String email;
        public String fullname;
        public String address;
        public String phone;
        public String role;
    }

    @Data
    public static class BookingRequest {
        @NotBlank
        public String serviceType;
        public String description;
        @NotNull
        public LocalDate serviceDate;
    }

    @Data
    @AllArgsConstructor
    public static class BookingResponse {
        public Long id;
        public String serviceType;
        public String description;
        public LocalDate serviceDate;
        public String status;
        public LocalDateTime createdAt;
        public String customerName;
        public String customerPhone;
        public String customerAddress;
        public String providerName;
    }

    @Data
    public static class StatusUpdateRequest {
        @NotBlank
        public String status;
    }

    @Data
    public static class AssignProviderRequest {
        @NotNull
        public Long providerId;
    }

    @Data
    @AllArgsConstructor
    public static class ServiceResponse {
        public Long id;
        public String name;
        public String description;
        public BigDecimal price;
        public String iconClass;
        public String category;
    }

    @Data
    @AllArgsConstructor
    public static class MessageResponse {
        public String message;
        public boolean success;
    }
}
