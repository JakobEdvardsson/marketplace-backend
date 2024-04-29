package org.example.marketplacebackend.repository;

import java.util.UUID;
import org.example.marketplacebackend.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {

}
