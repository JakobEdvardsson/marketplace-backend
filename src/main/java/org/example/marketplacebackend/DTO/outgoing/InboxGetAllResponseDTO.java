package org.example.marketplacebackend.DTO.outgoing;

import java.time.Instant;
import java.util.UUID;

public record InboxGetAllResponseDTO(UUID id, String message, boolean isRead, Instant sentAt, UUID productId) {}