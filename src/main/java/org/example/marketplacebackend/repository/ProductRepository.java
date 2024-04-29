package org.example.marketplacebackend.repository;

import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.Product;
import org.example.marketplacebackend.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
  void deleteByName(String name);
  List<Product> getAllByProductCategory(ProductCategory categoryId);
  Optional<Product> findProductByIdAndSeller(UUID id, Account seller);

}
