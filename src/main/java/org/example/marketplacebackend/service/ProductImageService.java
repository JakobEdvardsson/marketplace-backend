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
    BasicAWSCredentials creds = new BasicAWSCredentials(SPACE_ACCESS_KEY, SPACE_SECRET_KEY);
    AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(creds))
        .withEndpointConfiguration(new AmazonS3ClientBuilder.EndpointConfiguration(
            "https://ams3.digitaloceanspaces.com", "ams-3"))
        .build();

    String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

    if (fileName.contains("..")) {
      throw new IllegalArgumentException("Filename contains invalid path sequence " + fileName);
    }
    if (file.getBytes().length > (1024 * 1024)) {
      throw new MaxUploadSizeExceededException(file.getSize());
    }

    try (InputStream input = file.getInputStream()) {
      try {
        ImageIO.read(input);
      } catch (Exception e) {
        throw new FileNotFoundException("Nice try");
      }
    }

    String fileNameRandomized = UUID.randomUUID() + "_" + file.getOriginalFilename();
    ObjectMetadata metaData = new ObjectMetadata();
    metaData.setContentType(file.getContentType());
    metaData.setContentDisposition(fileNameRandomized);

    PutObjectRequest putObjectRequest = new PutObjectRequest("blocket-clone", fileNameRandomized,
        file.getInputStream(), metaData)
        .withCannedAcl(CannedAccessControlList.PublicRead);
    s3Client.putObject(putObjectRequest);

    ProductImage attachment = new ProductImage();
    Product product = productRepository.getReferenceById(productId);
    attachment.setProduct(product);
    attachment.setImageUrl(
        "https://blocket-clone.ams3.cdn.digitaloceanspaces.com/" + fileNameRandomized);

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
