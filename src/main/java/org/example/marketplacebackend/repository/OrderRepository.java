package org.example.marketplacebackend.repository;

import org.example.marketplacebackend.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderItem, UUID> {
  List<OrderItem> findAllByOrder_Id(UUID orderId);
}
