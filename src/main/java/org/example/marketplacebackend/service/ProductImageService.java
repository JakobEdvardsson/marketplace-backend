package org.example.marketplacebackend.service;

import org.example.marketplacebackend.model.ProductImage;
import org.example.marketplacebackend.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ProductImageService {
  private final ProductImageRepository productImageRepo;

  @Autowired
  public ProductImageService(ProductImageRepository productImageRepo) {
    this.productImageRepo = productImageRepo;
  }

  public List<ProductImage> uploadImages(UUID productId, String[] images) {
    List<ProductImage> uploadedImages = null;

    for (String image: images) {
    }

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
