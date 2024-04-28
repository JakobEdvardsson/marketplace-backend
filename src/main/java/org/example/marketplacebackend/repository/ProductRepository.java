package org.example.marketplacebackend.repository;

import org.example.marketplacebackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
  void deleteByName(String name);

}
