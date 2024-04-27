package org.example.marketplacebackend.DTO.incoming;

import java.util.UUID;

/**
 * DTO used for sending production registration form data
 * @param name
 * @param type
 * @param price
 * @param condition
 * @param description
 * @param seller
 * @param images
 * @param color [OPTIONAL]
 * @param productionYear [OPTIONAL]
 */
public record ProductDTO(String name, UUID type, int price, int condition,
                         String description, UUID seller, String[] images, int color, int productionYear) {

}
