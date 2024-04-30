package org.example.marketplacebackend.service;

import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.Product;
import org.example.marketplacebackend.model.ProductCategory;
import org.example.marketplacebackend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

  private final ProductRepository productRepo;

  public ProductService(ProductRepository productRepo) {
    this.productRepo = productRepo;
  }

  /**
   * @param id The id of the product to be returned.
   * @return The product with the given id if it exists else null.
   */
  public Product getProductOrNull(UUID id) {
    return productRepo.findById(id).orElse(null);
  }

  public List<Product> getAllByProductCategory(ProductCategory category) {
    return productRepo.getAllByProductCategory(category);
  }


  /**
   * Deletes the given product based on UUID
   * @param id UUID
   */
  public void deleteProductOrNull(UUID id) {
    Product product = productRepo.findById(id).orElse(null);
    assert product != null;
    productRepo.delete(product);
  }

  /**
   * Deletes the given product based on name
   * @param name product name
   */
  @Transactional
  public void deleteByName(String name) {
    productRepo.deleteByName(name);
  }

  /**
   * Saves the given product to the database.
   *
   * @param product The product to be saved.
   * @return The product.
   */
  public Product saveProduct(Product product) {
    return productRepo.save(product);
  }

  /**
   * Fetches all products in the database
   * @return a list of products
   */
  public List<Product> findAll() {
    return productRepo.findAll();
  }

  public List<Product> findTop20ByOrderByCreatedAtDesc() {
    return productRepo.findTop20ByOrderByCreatedAtDesc();
  }

  /**
   * Finds product based on UUID id and seller UUID
   * @param id UUID of product
   * @param seller UUID of product
   * @return a product
   */
  public Product findProductByIdAndSeller(UUID id, Account seller) {
    return productRepo.findProductByIdAndSeller(id, seller).orElse(null);
  }
}
