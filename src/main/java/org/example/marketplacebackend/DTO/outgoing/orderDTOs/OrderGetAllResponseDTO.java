package org.example.marketplacebackend.DTO.outgoing.orderDTOs;

import java.util.List;

public record OrderGetAllResponseDTO(List<OrderRegisteredResponseDTO> orders) {

}
