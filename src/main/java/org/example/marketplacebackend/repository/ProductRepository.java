package org.example.marketplacebackend.repository;

import org.example.marketplacebackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
  void deleteByName(String name);

  // void getAllByProductCategory(String productId);

}
