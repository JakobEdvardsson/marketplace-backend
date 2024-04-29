package org.example.marketplacebackend.DTO.outgoing;

import java.sql.Date;

/**
 * DTO used for sending a response when a user is registered.
 *
 * @param firstName
 * @param lastName
 * @param email
 * @param username
 * @param dateOfBirth
 */
public record UserRegisteredResponseDTO(String firstName, String lastName, String email,
                                        String username, Date dateOfBirth) {

}
