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

  List<Product> getAllByProductCategory(ProductCategory categoryId);

  Optional<Product> findProductByIdAndSeller(UUID id, Account seller);

  List<Product> findTop20ByOrderByCreatedAtDesc();

  @Query("""
      SELECT p FROM Product p WHERE p.seller = :seller AND p.status IN (0, 1)
      """)
  @EntityGraph(attributePaths = {"productCategory", "buyer"})
  List<Product> getActiveListingsHydrateProductCategoryAndBuyer(Account seller);

  @Query("""
      SELECT p FROM Product p WHERE p.seller = :seller AND p.status IN (2)
      """)
  @EntityGraph(attributePaths = {"productCategory"})
  List<Product> getSoldProducts(Account seller);
}
