package org.example.marketplacebackend.DTO.outgoing;

import java.util.UUID;
import org.example.marketplacebackend.model.ProductCategory;

public record WatchListResponseDTO(UUID id, ProductCategory productCategory) {

}