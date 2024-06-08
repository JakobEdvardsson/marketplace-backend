package org.example.marketplacebackend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.transaction.Transactional;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, UUID> {

  @Modifying
  @Transactional
  @Query("""
    UPDATE Product SET status=2, buyer=:deleted WHERE id = :id
    """)
  void updateProductByStatusAndBuyer(UUID id, Account deleted);

  @EntityGraph(attributePaths = {"productImages"})
  Optional<Product> findProductByIdAndSeller(UUID id, Account seller);

  @Query("""
      SELECT p FROM Product p WHERE p.seller = :seller AND p.status IN (0, 1)
      """)
  @EntityGraph(attributePaths = {"productCategory", "buyer", "productImages"})
  List<Product> getActiveListingsHydrateProductCategoryAndBuyer(Account seller);

  @Query("""
      SELECT p FROM Product p WHERE p.seller = :seller AND p.status IN (2)
      """)
  @EntityGraph(attributePaths = {"productCategory", "productImages"})
  List<Product> getSoldProducts(Account seller);

  @EntityGraph(attributePaths = {"productCategory", "productImages"})
  @Query("""
      SELECT p FROM Product p WHERE p.status = 0 AND p.productCategory.id in (:categories) ORDER BY p.createdAt DESC
      """)
  List<Product> getAllProductsByProvidedCategories(List<UUID> categories);
}
