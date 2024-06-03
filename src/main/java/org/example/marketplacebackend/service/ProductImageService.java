package org.example.marketplacebackend.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.example.marketplacebackend.model.Product;
import org.example.marketplacebackend.model.ProductImage;
import org.example.marketplacebackend.repository.ProductImageRepository;
import org.example.marketplacebackend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ProductImageService {
  @Value("${SPACE_ACCESS_KEY}")
  private String SPACE_ACCESS_KEY;
  @Value("${SPACES_SECRET_KEY}")
  private String SPACE_SECRET_KEY;
  private final ProductImageRepository productImageRepo;
  private final ProductRepository productRepository;

  @Autowired
  public ProductImageService(ProductImageRepository productImageRepo,
      ProductRepository productRepository) {
    this.productImageRepo = productImageRepo;
    this.productRepository = productRepository;
  }

  public ProductImage saveAttachment(UUID productId, MultipartFile file)
      throws IOException, IllegalArgumentException, MaxUploadSizeExceededException, SdkClientException {
    String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

    if (fileName.contains("..")) {
      throw new IllegalArgumentException("Filename contains invalid path sequence " + fileName);
    }
    if (file.getBytes().length > (1024 * 1024)) {
      throw new MaxUploadSizeExceededException(file.getSize());
    }

    String fileNameRandomized = UUID.randomUUID() + "_" + file.getOriginalFilename();

    try (InputStream input = file.getInputStream()) {
      try {
        BufferedImage image = ImageIO.read(input);
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (image != null) {
          ImageIO.write(image, fileExtension, new File("/opt/img/" + fileNameRandomized));
        }
        else {
          throw new FileNotFoundException("Nice try");
        }
      } catch (Exception e) {
        throw new FileNotFoundException("Nice try");
      }
    }

    ProductImage attachment = new ProductImage();
    Product product = productRepository.getReferenceById(productId);
    attachment.setProduct(product);
    attachment.setImageUrl(
        "http://localhost:8080/img/" + fileNameRandomized);

    return productImageRepo.save(attachment);
  }

  public List<ProductImage> saveFiles(UUID productId, MultipartFile[] images) throws Exception {
    List<ProductImage> uploadedImages = new ArrayList<>();

    for (MultipartFile image : images) {
      if (image.isEmpty()) {
        continue;
      }

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

  @Transactional
  public void deleteProductImageByImageUrl(String url) {
    productImageRepo.deleteProductImageByImageUrl(url);
  }
}
