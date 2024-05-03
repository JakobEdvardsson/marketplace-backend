package org.example.marketplacebackend.DTO.incoming;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record OrderItemDTO(@JsonProperty UUID productId) {

}
