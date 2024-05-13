package org.example.marketplacebackend.DTO.incoming;

public record PasswordChangeDTO(String oldPassword, String newPassword) {
}