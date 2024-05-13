package org.example.marketplacebackend.DTO.outgoing.productDTOs;

import java.time.Instant;
import java.util.UUID;

public record GetSoldProductResponseDTO(UUID productId, String name, String productCategoryName,
                                        Integer price, UUID buyer, Instant createdAt
) {

}
