package org.example.marketplacebackend.DTO.outgoing.productDTOs;

import java.util.UUID;

/**
 * DTO used for sending a response when a product is registered
 * @param name
 * @param type
 * @param price
 * @param condition
 * @param description
 * @param seller
 * @param imageUrls
 * @param color
 * @param productionYear
 */

public record ProductRegisteredResponseDTO(UUID id, String name, UUID type, int price, int condition,
                                           String description, UUID seller, String[] imageUrls,
                                           Integer color, Integer productionYear) {

}
