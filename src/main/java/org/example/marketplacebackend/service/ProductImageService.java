package org.example.marketplacebackend.service;

import org.example.marketplacebackend.model.ProductImage;
import org.example.marketplacebackend.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductImageService {
  private final ProductImageRepository productImageRepo;

  @Autowired
  public ProductImageService(ProductImageRepository productImageRepo) {
    this.productImageRepo = productImageRepo;
  }

  public List<ProductImage> uploadImages(String[] images) {

  }

}
