package org.example.marketplacebackend.controller;

import org.example.marketplacebackend.DTO.incoming.ProductDTO;
import org.example.marketplacebackend.DTO.outgoing.ProductGetResponseDTO;
import org.example.marketplacebackend.DTO.outgoing.ProductRegisteredResponseDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.Product;
import org.example.marketplacebackend.model.ProductImage;
import org.example.marketplacebackend.model.ProductCategory;
import org.example.marketplacebackend.service.CategoryService;
import org.example.marketplacebackend.service.ProductImageService;
import org.example.marketplacebackend.service.ProductService;
import org.example.marketplacebackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RequestMapping("v1/products")
@CrossOrigin(origins = {"http://localhost:3000, https://marketplace.johros.dev"}, allowCredentials = "true")
@Controller
public class ProductsController {

  private final CategoryService categoryService;
  private final ProductService productService;
  private final UserService userService;
  private final ProductImageService productImageService;

  public ProductsController(CategoryService categoryService,
      ProductService productService,
      UserService userService, ProductImageService productImageService) {
    this.categoryService = categoryService;
    this.productService = productService;
    this.userService = userService;
    this.productImageService = productImageService;
  }

  @PostMapping("")
  public ResponseEntity<?> uploadProduct(Principal principal,
      @RequestPart(value = "json") ProductDTO product,
      @RequestParam(value = "data") MultipartFile[] files
  ) throws Exception {
    String username = principal.getName();
    Account authenticatedUser = userService.getAccountOrException(username);

    Product productModel = new Product();
    productModel.setName(product.name());

    // User will grab existing product types from a list on the frontend
    ProductCategory productCategoryDB = categoryService.getReferenceById(product.productCategory());
    productModel.setProductCategory(productCategoryDB);

    productModel.setPrice(product.price());
    productModel.setCondition(product.condition());
    productModel.setIsPurchased(false);
    productModel.setDescription(product.description());
    productModel.setSeller(authenticatedUser);
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

    ProductRegisteredResponseDTO response = new ProductRegisteredResponseDTO(
        productDB.getId(),
        productDB.getName(), productDB.getProductCategory().getId(), productDB.getPrice(),
        productDB.getCondition(),
        productDB.getDescription(), productDB.getSeller().getId(), imageUrls,
        productDB.getColor() != null ? productDB.getColor() : null,
        productDB.getProductionYear() != null ? productDB.getProductionYear() : null
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("")
  public ResponseEntity<?> getProducts(
      @RequestParam(name = "category", required = false) String category) {
    List<Product> products;

    if (category == null) {
      products = productService.findTop20ByOrderByCreatedAtDesc();
      return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    ProductCategory productCategory = categoryService.findProductCategoryByNameOrNull(category);
    if (productCategory == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("That product category does not exist");
    }
    products = productService.getAllByProductCategory(productCategory);

    return ResponseEntity.status(HttpStatus.OK).body(products);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteProduct(Principal principal, @PathVariable UUID id) {
    Account authenticatedUser = userService.getAccountOrException(principal.getName());

    Product product = productService.findProductByIdAndSeller(id,
        authenticatedUser);

    if (product == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // If there are images we need to delete them first
    if (product.getProductImages() != null) {
      for (ProductImage image : product.getProductImages()) {
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
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    ProductGetResponseDTO response = new ProductGetResponseDTO(product);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
