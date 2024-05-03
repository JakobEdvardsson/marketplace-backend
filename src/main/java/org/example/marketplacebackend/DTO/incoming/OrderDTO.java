package org.example.marketplacebackend.DTO.incoming;

import java.util.List;

public record OrderDTO(List<OrderItemDTO> orderItemDTOS) {
}