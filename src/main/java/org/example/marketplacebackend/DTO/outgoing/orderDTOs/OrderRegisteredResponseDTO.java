package org.example.marketplacebackend.DTO.outgoing.orderDTOs;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderRegisteredResponseDTO(UUID orderId, Instant timeOfPurchase, List<OrderItemRegisteredResponseDTO> orderItems) {

}