package org.example.marketplacebackend.DTO.outgoing.productDTOs;

import org.example.marketplacebackend.DTO.incoming.ProductCategoryDTO;
import java.time.Instant;
import java.util.UUID;

public record ProductGetResponseDTO(UUID productId, String name, ProductCategoryDTO productCategory,
                                    Integer price, Integer condition, Integer status,
                                    String description, UUID seller, UUID buyer,
                                    Integer color, Integer productionYear, Instant createdAt, String[] imageUrls) {

}
