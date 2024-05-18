package org.example.marketplacebackend.DTO.outgoing.productDTOs;

import java.util.UUID;

/**
 * DTO used for sending production registration form data
 *
 * @param name
 * @param productCategory
 * @param price
 * @param condition
 * @param description
 * @param color          [OPTIONAL]
 * @param productionYear [OPTIONAL]
 * @param productId
 */
public record ProductDTO(String name, UUID productCategory, int price, int condition,
                         String description, Integer color,
                         Integer productionYear, UUID productId) {

}