package org.example.marketplacebackend.service;

import org.example.marketplacebackend.model.ProductCategory;
import org.example.marketplacebackend.repository.ProductCategoryRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class CategoryService {

  ProductCategoryRepository productCategoryRepo;

  public CategoryService(
      ProductCategoryRepository productCategoryRepo) {
    this.productCategoryRepo = productCategoryRepo;
  }

  public ProductCategory getReferenceById(UUID id) {
    return productCategoryRepo.getReferenceById(id);
  }

  public ProductCategory findProductCategoryByNameOrNull(String name) {
    return productCategoryRepo.findByName(name).orElse(null);
  }

}
