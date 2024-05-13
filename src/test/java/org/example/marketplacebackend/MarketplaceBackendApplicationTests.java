package org.example.marketplacebackend;

import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.Product;
import org.example.marketplacebackend.model.ProductImage;
import org.example.marketplacebackend.model.ProductCategory;
import org.example.marketplacebackend.model.ProductStatus;
import org.example.marketplacebackend.repository.AccountRepository;
import org.example.marketplacebackend.repository.ProductCategoryRepository;
import org.example.marketplacebackend.repository.ProductImageRepository;
import org.example.marketplacebackend.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.sql.Date;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
class MarketplaceBackendApplicationTests {
  @Container
  private static final PostgreSQLContainer<?> DB = new PostgreSQLContainer<>(
      "postgres:16-alpine"
  )
      .withInitScript("schema.sql");
  @DynamicPropertySource
  private static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", DB::getJdbcUrl);
    registry.add("spring.datasource.username", DB::getUsername);
    registry.add("spring.datasource.password", DB::getPassword);
  }

  private final ProductRepository products;
  private final AccountRepository accountsRepo;
  private final ProductCategoryRepository productCategoryRepository;
  private final ProductImageRepository imageRepo;

  @Autowired
  public MarketplaceBackendApplicationTests(ProductRepository products, AccountRepository accountsRepo,
      ProductCategoryRepository productCategoryRepository, ProductImageRepository imageRepo) {
    this.products = products;
    this.accountsRepo = accountsRepo;
    this.productCategoryRepository = productCategoryRepository;
    this.imageRepo = imageRepo;
  }

  @Test
  void contextLoads() {
  }

  @Test
  void testProductModel() {
    Account seller = new Account();
    Account buyer = new Account();

    seller.setFirstName("Test");
    seller.setLastName("Seller");
    seller.setDateOfBirth(new Date(1993 - 03 - 11));
    seller.setEmail("seller@mail.com");
    seller.setPassword("test123");
    seller.setUsername("ffsFILIP");

    buyer.setFirstName("OliverTest");
    buyer.setLastName("BergTest");
    buyer.setDateOfBirth(new Date(2000 - 01 - 03));
    buyer.setEmail("obbobo@testbuyer.com");
    buyer.setPassword("rorortest");
    buyer.setUsername("oliver");

    var savedBuyer = accountsRepo.save(buyer);
    var savedSeller = accountsRepo.save(seller);

    ProductCategory productCategory = new ProductCategory();
    productCategory.setName("TEST_CATEGORY");
    var savedProductType = productCategoryRepository.save(productCategory);

    Product product = new Product();
    product.setName("Macbook");
    product.setSeller(savedSeller);
    product.setBuyer(null);
    product.setDescription("hi description");
    product.setStatus(ProductStatus.AVAILABLE.ordinal());
    product.setColor(1);
    product.setProductCategory(savedProductType);
    product.setPrice(1337);
    product.setProductionYear(2000);
    product.setCondition(0);
    var savedProduct = products.save(product);

    ProductImage image = new ProductImage();
    image.setImageUrl("macbook.jpg");
    image.setProduct(savedProduct);
    var savedImage = imageRepo.save(image);

    imageRepo.delete(savedImage);
    products.delete(savedProduct);
    productCategoryRepository.delete(savedProductType);
    accountsRepo.delete(savedBuyer);
    accountsRepo.delete(savedSeller);
  }
}