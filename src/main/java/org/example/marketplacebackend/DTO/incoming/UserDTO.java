package org.example.marketplacebackend.DTO.incoming;

import java.sql.Date;

/**
 * DTO used for sending user registration form data.
 *
 * @param firstName
 * @param lastName
 * @param email
 * @param username
 * @param password
 * @param dateOfBirth
 */
public record UserDTO(String firstName, String lastName, String email, String username,
                      String password, Date dateOfBirth) {

}
