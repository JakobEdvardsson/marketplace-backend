package org.example.marketplacebackend.DTO.outgoing;

import java.sql.Date;

public record MyProfileResponseDTO(String firstName, String lastName, Date dateOfBirth,
                                   String username, String email) {
}
