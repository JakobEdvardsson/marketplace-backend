package org.example.marketplacebackend.DTO.outgoing.productDTOs;

import org.example.marketplacebackend.DTO.outgoing.ProfileResponseDTO;
import java.time.Instant;
import java.util.UUID;

public record ActiveListingDTO(UUID id, String productName, String productCategoryName, Integer price,
                               Integer productStatus, Instant createdAt, ProfileResponseDTO buyer) {

}
