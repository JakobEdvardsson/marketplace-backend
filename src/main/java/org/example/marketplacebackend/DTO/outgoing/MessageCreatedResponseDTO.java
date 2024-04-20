package org.example.marketplacebackend.DTO.outgoing;

import org.example.marketplacebackend.model.Account;

public record MessageCreatedResponseDTO (Account account, String message, boolean isRead) { }
