package org.example.marketplacebackend.controller;

import org.example.marketplacebackend.DTO.incoming.ProductDTO;
import org.example.marketplacebackend.DTO.outgoing.ProductGetResponseDTO;
import org.example.marketplacebackend.DTO.outgoing.ProductRegisteredResponseDTO;
import org.example.marketplacebackend.model.Product;
import org.example.marketplacebackend.model.ProductImage;
import org.example.marketplacebackend.model.ProductCategory;
import org.example.marketplacebackend.repository.ProductCategoryRepository;
import org.example.marketplacebackend.service.ProductImageService;
import org.example.marketplacebackend.service.ProductService;
import org.example.marketplacebackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@RequestMapping("v1/products")
@CrossOrigin(origins = "localhost:3000", allowCredentials = "true")
@RestController
public class ProductsController {

  private final ProductCategoryRepository productTypeRepo;
  private final ProductService productService;
  private final UserService userService;
  private final ProductImageService productImageService;

  public ProductsController(ProductCategoryRepository productTypeRepo, ProductService productService,
      UserService userService, ProductImageService productImageService) {
    this.productTypeRepo = productTypeRepo;
    this.productService = productService;
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
    ProductCategory productCategoryDB = productTypeRepo.getReferenceById(product.type());
    productModel.setType(productCategoryDB);

    productModel.setPrice(product.price());
    productModel.setCondition(product.condition());
    productModel.setIsPurchased(false);
    productModel.setDescription(product.description());
    productModel.setSeller(userService.getAccountOrNull(product.seller()));
    productModel.setColor(product.color());
    productModel.setProductionYear(product.productionYear());

    // Save all data to DB
    Product productDB = productService.saveProduct(productModel);

    // Upload images and add to product model
    List<ProductImage> uploadedImages = productImageService.saveFiles(productDB.getId(),
        files);
    productModel.setProductImages(uploadedImages);

    // Get all image urls from all image objects
    String[] imageUrls = productImageService.productImagesToImageUrls(uploadedImages);

    ProductRegisteredResponseDTO productRegisteredResponseDTO;
    if (productDB.getColor() != null || productDB.getProductionYear() != null) {
      productRegisteredResponseDTO = new ProductRegisteredResponseDTO(productDB.getId(),
          productDB.getName(), productDB.getType().getId(), productDB.getPrice(),
          productDB.getCondition(),
          productDB.getDescription(), productDB.getSeller().getId(), imageUrls,
          productDB.getColor(), productDB.getProductionYear()
      );
    } else {
      productRegisteredResponseDTO = new ProductRegisteredResponseDTO(productDB.getId(),
          productDB.getName(), productDB.getType().getId(), productDB.getPrice(),
          productDB.getCondition(),
          productDB.getDescription(), productDB.getSeller().getId(), imageUrls,
          null, null);
    }

    return ResponseEntity.status(HttpStatus.CREATED).body(productRegisteredResponseDTO);
  }

  @GetMapping("")
  public ResponseEntity<?> getProducts() {
    List<Product> products = productService.findAll();
    return ResponseEntity.status(HttpStatus.OK).body(products);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteProduct(@PathVariable UUID id) {
    Product product = productService.getProductOrNull(id);

    if (product == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No product with that ID exists.");
    }

    // If there are images we need to delete them first
    if (product.getProductImages() != null) {
      for (ProductImage image: product.getProductImages()) {
        productImageService.deleteImage(image);
      }
    }

    productService.deleteProductOrNull(id);
    return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getProduct(@PathVariable UUID id) {
    Product product = productService.getProductOrNull(id);

    if (product == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No product with that ID exists.");
    }

    ProductGetResponseDTO productGetResponseDTO = new ProductGetResponseDTO(product);
    return ResponseEntity.status(HttpStatus.OK).body(productGetResponseDTO);
  }
}
