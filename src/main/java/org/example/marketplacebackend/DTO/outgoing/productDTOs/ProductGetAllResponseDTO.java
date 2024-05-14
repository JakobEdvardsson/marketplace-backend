package org.example.marketplacebackend.DTO.outgoing.productDTOs;

import java.util.List;

public record ProductGetAllResponseDTO(List<ProductGetResponseDTO> products) {

}
