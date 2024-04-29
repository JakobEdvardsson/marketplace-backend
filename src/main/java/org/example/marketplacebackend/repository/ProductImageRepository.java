package org.example.marketplacebackend.repository;

import org.example.marketplacebackend.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {
  void deleteProductImageByImageUrl(String imageUrl);
}
