package org.example.marketplacebackend.repository;

import org.example.marketplacebackend.model.ProductOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface OrderHistoryRepository extends JpaRepository<ProductOrder, UUID> {

  @EntityGraph(attributePaths = {"orderItems.product"})
  List<ProductOrder> findAllByBuyer_Id(UUID buyerId);

  Optional<ProductOrder> getProductOrderByBuyer_IdAndId(UUID buyerId, UUID id);

  @EntityGraph(attributePaths = {"orderItems.product"})
  @Query("""
      SELECT p FROM ProductOrder p WHERE p.buyer.id = :buyerId AND p.timeOfPurchase BETWEEN :start AND :end
""")
  List<ProductOrder> findAllOrdersByPeriod(UUID buyerId, Instant start, Instant end);

}
