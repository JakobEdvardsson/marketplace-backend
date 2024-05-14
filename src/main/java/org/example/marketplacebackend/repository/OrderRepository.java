package org.example.marketplacebackend.repository;

import java.util.Optional;
import java.util.UUID;
import org.example.marketplacebackend.model.OrderItem;
import org.example.marketplacebackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderItem, UUID> {
  Optional<OrderItem> findByProduct(Product product);
}
