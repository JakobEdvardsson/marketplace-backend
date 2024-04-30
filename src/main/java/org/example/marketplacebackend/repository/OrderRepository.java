package org.example.marketplacebackend.repository;

import org.example.marketplacebackend.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderItem, UUID> {

}
