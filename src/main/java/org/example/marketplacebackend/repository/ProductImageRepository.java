package org.example.marketplacebackend.repository;

import org.example.marketplacebackend.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {

}
