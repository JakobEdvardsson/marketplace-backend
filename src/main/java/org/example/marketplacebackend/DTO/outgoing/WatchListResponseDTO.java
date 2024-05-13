package org.example.marketplacebackend.DTO.outgoing;

import java.util.UUID;
import org.example.marketplacebackend.DTO.incoming.ProductCategoryDTO;

public record WatchListResponseDTO(UUID id, ProductCategoryDTO productCategory) {

}