package org.example.marketplacebackend.DTO.incoming;

import org.example.marketplacebackend.model.OrderItem;
import java.util.List;

public record OrderDTO(List<OrderItem> orderItems) {

}
