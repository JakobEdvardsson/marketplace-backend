package org.example.marketplacebackend.DTO.outgoing.productDTOs;

import org.example.marketplacebackend.model.Product;
import java.util.List;

public record ProductGetAllResponseDTO(List<Product> products) {

}
