package org.example.marketplacebackend.repository;

import org.example.marketplacebackend.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {

  Optional<ProductCategory> findById(UUID id);

  Optional<ProductCategory> findByName(String name);
}
