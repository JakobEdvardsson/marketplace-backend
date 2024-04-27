package org.example.marketplacebackend.repository;

import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ProductTypeRepository extends JpaRepository<ProductType, UUID> {
  Optional<ProductType> findById(UUID id);
}
