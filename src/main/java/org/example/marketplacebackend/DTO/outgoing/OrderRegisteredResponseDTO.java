package org.example.marketplacebackend.DTO.outgoing;

import org.example.marketplacebackend.model.ProductOrder;

public record OrderRegisteredResponseDTO(ProductOrder order) {

}
