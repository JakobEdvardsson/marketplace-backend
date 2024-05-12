package org.example.marketplacebackend.DTO.outgoing.orderDTOs;

import java.util.UUID;

public record OrderItemRegisteredResponseDTO(UUID productId, String productName, int price) {

}
