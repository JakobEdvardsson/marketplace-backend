package org.example.marketplacebackend.service;

import org.example.marketplacebackend.model.Product;
import org.example.marketplacebackend.model.ProductImage;
import org.example.marketplacebackend.repository.ProductImageRepository;
import org.example.marketplacebackend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ProductImageService {

  private final ProductImageRepository productImageRepo;
  private final ProductRepository productRepository;

  @Autowired
  public ProductImageService(ProductImageRepository productImageRepo,
      ProductRepository productRepository) {
    this.productImageRepo = productImageRepo;
    this.productRepository = productRepository;
  }

  public ProductImage saveAttachment(UUID productId, MultipartFile file) throws Exception {
    String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

    try {
      if (fileName.contains("..")) {
        throw new Exception("Filename contains invalid path sequence " + fileName);
      }
      if (file.getBytes().length > (1024 * 1024)) {
        throw new Exception("File size exceeds maximum limit");
      }

      ProductImage attachment = new ProductImage();
      Product product = productRepository.getReferenceById(productId);
      attachment.setProduct(product);
      String imageUrl = UUID.randomUUID() + "_" + file.getOriginalFilename();
      attachment.setImageUrl(imageUrl);

      Path uploadPath = Path.of("src/main/resources/images/");
      Path filePath = uploadPath.resolve(imageUrl);
      Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

      return productImageRepo.save(attachment);
    } catch (MaxUploadSizeExceededException e) {
      throw new MaxUploadSizeExceededException(file.getSize());
    } catch (Exception e) {
      throw new Exception("Could not save File: " + fileName + ", " + e.getMessage());
    }
  }

  public List<ProductImage> saveFiles(UUID productId, MultipartFile[] images) throws Exception {
    List<ProductImage> uploadedImages = new ArrayList<>();

    File directory = new File("src/main/resources/images/");
    if (!directory.exists()) {
      directory.mkdir();
    }

    for (MultipartFile image : images) {
      ProductImage uploadedImage = saveAttachment(productId, image);
      uploadedImages.add(uploadedImage);
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

  /**
   * Deletes the given image to the database.
   *
   * @param image The image to be deleted.
   */
  public void deleteImage(ProductImage image) {
    productImageRepo.delete(image);
  }

}
