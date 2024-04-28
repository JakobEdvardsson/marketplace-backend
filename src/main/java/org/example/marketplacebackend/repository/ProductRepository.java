package org.example.marketplacebackend.repository;

import org.example.marketplacebackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
  // Transactional should potentially be in the service layer
  // but right now we don't have a service layer for this repository
  @Transactional
  void deleteByName(String name);

}
