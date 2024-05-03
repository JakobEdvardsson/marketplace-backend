package org.example.marketplacebackend.DTO.incoming;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record OrderDTO(@JsonProperty List<OrderItemDTO> orderItemDTOS) {
}