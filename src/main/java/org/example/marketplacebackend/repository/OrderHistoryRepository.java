package org.example.marketplacebackend.repository;

import org.example.marketplacebackend.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrderHistoryRepository extends JpaRepository<ProductOrder, UUID> {

}
