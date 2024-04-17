package org.example.marketplacebackend;

import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.Product;
import org.example.marketplacebackend.model.ProductImage;
import org.example.marketplacebackend.model.ProductType;
import org.example.marketplacebackend.repository.AccountRepository;
import org.example.marketplacebackend.repository.ProductImageRepository;
import org.example.marketplacebackend.repository.ProductRepository;
import org.example.marketplacebackend.repository.ProductTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class MarketplaceBackendApplicationTests {
  private final ProductRepository products;
  private final AccountRepository accountsRepo;
  private final ProductTypeRepository typeRepo;
  private final ProductImageRepository imageRepo;
  @Autowired
  public MarketplaceBackendApplicationTests(ProductRepository products, AccountRepository accountsRepo,
      ProductTypeRepository typeRepo, ProductImageRepository imageRepo) {
    this.products = products;
    this.accountsRepo = accountsRepo;
    this.typeRepo = typeRepo;
    this.imageRepo = imageRepo;
  }

  @Test
  void contextLoads() {
  }

  @Test
  void testProductModel() {
    Account seller = new Account();
    Account buyer = new Account();

    seller.setFirst_name("Test");
    seller.setLast_name("Seller");
    seller.setDate_of_birth(new Date(1993-03-11));
    seller.setEmail("test@mail.com");
    seller.setPassword("test123");
    seller.setUsername("ffsFILIP");

    buyer.setFirst_name("OliverTest");
    buyer.setLast_name("BergTest");
    buyer.setDate_of_birth(new Date(2000-01-03));
    buyer.setEmail("obbobo@testbuyer.com");
    buyer.setPassword("rorortest");
    buyer.setUsername("oliver");

    var savedBuyer = accountsRepo.save(buyer);
    var savedSeller = accountsRepo.save(seller);

    ProductType productType = new ProductType();
    productType.setName("TEST_CATEGORY");
    var savedProductType = typeRepo.save(productType);

    Product product = new Product();
    product.setName("Macbook");
    product.setSeller(savedSeller);
    product.setBuyer(null);
    product.setDescription("hi description");
    product.setIsPurchased(false);
    product.setColor(1);
    product.setType(savedProductType);
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
    typeRepo.delete(savedProductType);
    accountsRepo.delete(savedBuyer);
    accountsRepo.delete(savedSeller);
  }
}