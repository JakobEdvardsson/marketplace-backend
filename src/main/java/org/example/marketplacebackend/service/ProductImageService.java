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
    List<ProductImage> uploadedImages = null;

    return uploadedImages;
  }

  public String[] productImagesToImageUrls(List<ProductImage> images) {
    String[] imageUrls = new String[images.size()];

    for (int i = 0; i < images.size(); i++) {
      imageUrls[i] = images.get(i).getImageUrl();
    }

    return imageUrls;
  }

}
