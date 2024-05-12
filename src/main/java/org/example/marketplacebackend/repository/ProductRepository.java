package org.example.marketplacebackend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.Product;
import org.example.marketplacebackend.model.ProductCategory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, UUID> {

  void deleteByName(String name);

  @EntityGraph(attributePaths = {"productImages"})
  List<Product> getAllByProductCategory(ProductCategory categoryId);

  @EntityGraph(attributePaths = {"productImages"})
  Optional<Product> findProductByIdAndSeller(UUID id, Account seller);

  @Query("SELECT p.id FROM Product p ORDER BY p.createdAt DESC LIMIT 20")
  List<UUID> findTop20ProductIds();

  @EntityGraph(attributePaths = {"productImages"})
  List<Product> findByIdIn(List<UUID> ids);

}
