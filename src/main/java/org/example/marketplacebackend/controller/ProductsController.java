package org.example.marketplacebackend.controller;

import org.example.marketplacebackend.DTO.incoming.ProductDTO;
import org.example.marketplacebackend.DTO.outgoing.ProductRegisteredResponseDTO;
import org.example.marketplacebackend.model.Product;
import org.example.marketplacebackend.model.ProductImage;
import org.example.marketplacebackend.model.ProductType;
import org.example.marketplacebackend.repository.ProductRepository;
import org.example.marketplacebackend.repository.ProductTypeRepository;
import org.example.marketplacebackend.service.ProductImageService;
import org.example.marketplacebackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RequestMapping("v1/products")
@CrossOrigin(origins = "localhost:3000", allowCredentials = "true")
@RestController
public class ProductsController {

  private final ProductTypeRepository productTypeRepo;
  private final ProductRepository productRepo;
  private final UserService userService;
  private final ProductImageService productImageService;

  public ProductsController(ProductTypeRepository productTypeRepo, ProductRepository productRepo,
      UserService userService, ProductImageService productImageService) {
    this.productTypeRepo = productTypeRepo;
    this.productRepo = productRepo;
    this.userService = userService;
    this.productImageService = productImageService;
  }

  @PostMapping("")
  public ResponseEntity<?> uploadProduct(
      @RequestPart(value = "json") ProductDTO product,
      @RequestParam(value = "data") MultipartFile[] files
  ) throws Exception {
    Product productModel = new Product();
    productModel.setName(product.name());

    // User will grab existing product types from a list on the frontend
    ProductType productTypeDB = productTypeRepo.getReferenceById(product.type());
    productModel.setType(productTypeDB);

    productModel.setPrice(product.price());
    productModel.setCondition(product.condition());
    productModel.setIsPurchased(false);
    productModel.setDescription(product.description());
    productModel.setSeller(userService.getAccountOrNull(product.seller()));
    productModel.setColor(product.color());
    productModel.setProductionYear(product.productionYear());

    // Save all data to DB
    Product productDB = productRepo.save(productModel);

    // Upload images and add to product model
    List<ProductImage> uploadedImages = productImageService.saveFiles(productDB.getId(),
        files);
    productModel.setProductImages(uploadedImages);

    // Get all image urls from all image objects
    String[] imageUrls = productImageService.productImagesToImageUrls(uploadedImages);

    ProductRegisteredResponseDTO productRegisteredResponseDTO = null;
    if (productDB.getColor() != null || productDB.getProductionYear() != null) {
      productRegisteredResponseDTO = new ProductRegisteredResponseDTO(
          productDB.getName(), productDB.getType().getId(), productDB.getPrice(),
          productDB.getCondition(),
          productDB.getDescription(), productDB.getSeller().getId(), imageUrls,
          productDB.getColor(), productDB.getProductionYear()
      );
    } else {
      productRegisteredResponseDTO = new ProductRegisteredResponseDTO(
          productDB.getName(), productDB.getType().getId(), productDB.getPrice(),
          productDB.getCondition(),
          productDB.getDescription(), productDB.getSeller().getId(), imageUrls,
          null, null);
    }

    return ResponseEntity.status(HttpStatus.CREATED).body(productRegisteredResponseDTO);
  }
}
