package org.example.marketplacebackend.DTO.outgoing;

import java.util.UUID;

/**
 * DTO used for sending a response when a product is registered
 * @param name
 * @param type
 * @param price
 * @param condition
 * @param description
 * @param seller
 * @param images
 * @param color
 * @param productionYear
 */

public record ProductRegisteredResponseDTO(String name, UUID type, int price, int condition,
                                           String description, UUID seller, String[] images,
                                           int color, int productionYear) {

}
