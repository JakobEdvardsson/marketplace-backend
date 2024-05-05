package org.example.marketplacebackend.DTO.outgoing;

import org.example.marketplacebackend.model.ProductCategory;
import java.util.UUID;

public record WatchListResponseDTO(UUID id, ProductCategory productCategory) {}