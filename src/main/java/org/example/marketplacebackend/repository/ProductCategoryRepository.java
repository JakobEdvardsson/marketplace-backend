package org.example.marketplacebackend.repository;

import org.example.marketplacebackend.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.UUID;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {

  Optional<ProductCategory> findById(UUID id);

  @Query("""
      SELECT pc FROM ProductCategory pc WHERE pc.name = :name
      """)
  Optional<ProductCategory> findByName(String name);
}
