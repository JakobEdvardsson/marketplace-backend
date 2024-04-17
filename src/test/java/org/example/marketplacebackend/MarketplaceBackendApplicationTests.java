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
  @Autowired
  private final ProductRepository products;
  @Autowired
  private final AccountRepository accountsRepo;
  @Autowired
  private final ProductTypeRepository typeRepo;
  @Autowired
  private final ProductImageRepository imageRepo;
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

    buyer.setFirst_name("OliverTest");
    buyer.setLast_name("BergTest");
    buyer.setDate_of_birth(new Date(2000-01-03));
    buyer.setEmail("obbobo@testbuyer.com");
    buyer.setPassword("rorortest");

    accountsRepo.save(buyer);
    accountsRepo.save(seller);

    ProductType cars = new ProductType();
    cars.setName("cars");
    typeRepo.save(cars);

    Product product = new Product();
    product.setName("Macbook");
    product.setSeller(seller);
    product.setBuyer(buyer);
    product.setDescription("hi description");
    product.setIsPurchased(false);

    ProductImage image = new ProductImage();
    image.setImageUrl("macbook.jpg");
    image.setProduct(product);
    imageRepo.save(image);

    List<ProductImage> images = new ArrayList<>();
    images.add(image);
    product.setProductImages(images);

    product.setColor(1);
    product.setProductionYear(2000);
    products.save(product);

    products.delete(product);
    imageRepo.delete(image);
    typeRepo.delete(cars);
    accountsRepo.delete(buyer);
    accountsRepo.delete(seller);
  }
}