package org.example.marketplacebackend.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.example.marketplacebackend.DTO.incoming.ProductCategoryDTO;
import org.example.marketplacebackend.DTO.incoming.ProductDTO;
import org.example.marketplacebackend.DTO.outgoing.ProfileResponseDTO;
import org.example.marketplacebackend.DTO.outgoing.productDTOs.ActiveListingDTO;
import org.example.marketplacebackend.DTO.outgoing.productDTOs.ActiveListingsDTO;
import org.example.marketplacebackend.DTO.outgoing.productDTOs.GetAllSoldProductsResponseDTO;
import org.example.marketplacebackend.DTO.outgoing.productDTOs.GetSoldProductResponseDTO;
import org.example.marketplacebackend.DTO.outgoing.productDTOs.ProductGetAllResponseDTO;
import org.example.marketplacebackend.DTO.outgoing.productDTOs.ProductGetResponseDTO;
import org.example.marketplacebackend.DTO.outgoing.productDTOs.ProductRegisteredResponseDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.Inbox;
import org.example.marketplacebackend.model.Product;
import org.example.marketplacebackend.model.ProductCategory;
import org.example.marketplacebackend.model.ProductImage;
import org.example.marketplacebackend.model.ProductStatus;
import org.example.marketplacebackend.model.Watchlist;
import org.example.marketplacebackend.repository.WatchListRepository;
import org.example.marketplacebackend.service.CategoryService;
import org.example.marketplacebackend.service.InboxService;
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
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("v1/products")
@CrossOrigin(origins = {
    "http://localhost:3000, https://marketplace.johros.dev"}, allowCredentials = "true")
@Controller
public class ProductsController {

  private final CategoryService categoryService;
  private final ProductService productService;
  private final UserService userService;
  private final ProductImageService productImageService;
  private final InboxService inboxService;
  private final WatchListRepository watchListRepository;

  public ProductsController(CategoryService categoryService,
      ProductService productService,
      UserService userService, ProductImageService productImageService,
      InboxService inboxService,
      WatchListRepository watchListRepository) {
    this.categoryService = categoryService;
    this.productService = productService;
    this.userService = userService;
    this.productImageService = productImageService;
    this.inboxService = inboxService;
    this.watchListRepository = watchListRepository;
  }

  @PostMapping("")
  public ResponseEntity<?> uploadProduct(Principal principal,
      @RequestPart(value = "json") ProductDTO product,
      @RequestParam(value = "data") MultipartFile[] files
  ) throws Exception {

    if (principal == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String username = principal.getName();
    Account authenticatedUser = userService.getAccountOrException(username);

    Product productModel = new Product();
    productModel.setName(product.name());

    // User will grab existing product types from a list on the frontend
    ProductCategory productCategoryDB = categoryService.getReferenceById(product.productCategory());
    productModel.setProductCategory(productCategoryDB);

    productModel.setPrice(product.price());
    productModel.setCondition(product.condition());
    productModel.setStatus(ProductStatus.AVAILABLE.ordinal());
    productModel.setDescription(product.description());
    productModel.setSeller(authenticatedUser);
    productModel.setColor(product.color());
    productModel.setProductionYear(product.productionYear());

    // Save all data to DB
    Product productDB = productService.saveProduct(productModel);

    // Upload images and add to product model
    List<ProductImage> uploadedImages;
    try {
      uploadedImages = productImageService.saveFiles(productDB.getId(),
          files);
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } catch (MaxUploadSizeExceededException e) {
      return ResponseEntity.status(413).build();
    }
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

    List<String> watchLists =  watchListRepository.findByProductCategory(productDB.getProductCategory());
    List<Inbox> inboxes = new ArrayList<>();
    for (String watchList : watchLists) {
      Account account = new Account();
      account.setId(UUID.fromString(watchList));

      Inbox inbox = new Inbox();
      inbox.setProduct(productDB);
      inbox.setMessage("New product added from category: " + productDB.getProductCategory().getName());
      inbox.setReceiver(account);
      inbox.setIsRead(false);
      inboxes.add(inbox);
    }

    inboxService.saveAll(inboxes);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("")
  public ResponseEntity<?> getProducts(
      @RequestParam(name = "category", required = false) String category,
      @RequestParam(name = "minPrice", required = false) Integer minPrice,
      @RequestParam(name = "maxPrice", required = false) Integer maxPrice,
      @RequestParam(name = "condition", required = false) Integer condition,
      @RequestParam(name = "sort", required = false) Integer sort,
      @RequestParam(name = "query", required = false) String query
      ) {
    ProductGetAllResponseDTO response = productService.getProducts(category, minPrice, maxPrice, condition,
        sort, query);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteProduct(Principal principal, @PathVariable UUID id) {
    if (principal == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

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

    productService.setStatusSoldAndBuyerToDeleted(id);
    return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getProduct(@PathVariable UUID id) {
    Product product = productService.getProductOrNull(id);

    if (product == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    ProductCategory productCategory = product.getProductCategory();
    ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO(productCategory.getId(),
        productCategory.getName());
    ProductGetResponseDTO response = new ProductGetResponseDTO(product.getId(),
        product.getName(), productCategoryDTO, product.getPrice(), product.getCondition(),
        product.getStatus(), product.getDescription(), product.getSeller().getId(),
        product.getBuyer() != null ? product.getBuyer().getId() : null,
        product.getColor(), product.getProductionYear(), product.getCreatedAt(),
        productImageService.productImagesToImageUrls(product.getProductImages())
    );

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/my-subscribed-categories")
  public ResponseEntity<?> getMySubscribedCategories(Principal principal) {
    if (principal == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    Account authenticatedUser = userService.getAccountOrException(principal.getName());
    List<Watchlist> watchlists = watchListRepository.findAllBySubscriber(authenticatedUser);
    List<UUID> categories = new ArrayList<>();
    for (Watchlist watchlist: watchlists) {
      categories.add(watchlist.getProductCategory().getId());
    }

    ProductGetAllResponseDTO response = productService.getAllProductsByProvidedCategories(categories);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/my-active-listings")
  public ResponseEntity<?> getMyListings(Principal principal) {
    if (principal == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    Account authenticatedUser = userService.getAccountOrException(principal.getName());

    List<Product> activeListings = productService.getActiveListings(authenticatedUser);

    ActiveListingsDTO listings = new ActiveListingsDTO(activeListings
        .stream()
        .map(product -> new ActiveListingDTO(
                product.getId(),
                product.getName(),
                product.getProductCategory().getName(),
                product.getPrice(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getBuyer() != null ?
                    new ProfileResponseDTO(
                        product.getBuyer().getFirstName(),
                        product.getBuyer().getLastName(),
                        product.getBuyer().getUsername())
                    : null
            )
        )
        .toList()
    );

    return ResponseEntity.status(HttpStatus.OK).body(listings);
  }

  @GetMapping("/my-sold-products")
  public ResponseEntity<?> getAllSoldProducts(Principal principal) {
    if (principal == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String username = principal.getName();

    Account authenticatedUser = userService.getAccountOrException(username);
    List<Product> products = productService.getSoldProducts(authenticatedUser);

    List<GetSoldProductResponseDTO> soldProducts = new ArrayList<>();
    for (Product product : products) {
      GetSoldProductResponseDTO soldProduct = new GetSoldProductResponseDTO(
          product.getId(), product.getName(), product.getProductCategory().getName(),
          product.getPrice(), product.getBuyer().getId(), product.getCreatedAt()
      );
      soldProducts.add(soldProduct);
    }
    GetAllSoldProductsResponseDTO response = new GetAllSoldProductsResponseDTO(soldProducts);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
