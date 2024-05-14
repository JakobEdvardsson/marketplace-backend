package org.example.marketplacebackend.service;

import org.example.marketplacebackend.DTO.incoming.ProductCategoryDTO;
import org.example.marketplacebackend.DTO.outgoing.productDTOs.ProductGetAllResponseDTO;
import org.example.marketplacebackend.DTO.outgoing.productDTOs.ProductGetResponseDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.Product;
import org.example.marketplacebackend.model.ProductCategory;
import org.example.marketplacebackend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
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

  public ProductGetAllResponseDTO getAllByProductCategory(ProductCategory category) {
    List<Product> products = productRepo.getAllByProductCategory(category);
    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();

    convertProductsToDTO(products, productGetResponseDTOList);

    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO findTop20ByOrderByCreatedAtDesc() {
    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();
    List<Product> products = productRepo.findTop20ByOrderByCreatedAtDesc();

    convertProductsToDTO(products, productGetResponseDTOList);

      return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  private void convertProductsToDTO(List<Product> products,
      List<ProductGetResponseDTO> productGetResponseDTOList) {
    for (Product product : products) {
      ProductCategory productCategoryDb = product.getProductCategory();
      ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO(productCategoryDb.getId(),
          productCategoryDb.getName());
      ProductGetResponseDTO productGetResponseDTO = new ProductGetResponseDTO(product.getId(),
          product.getName(), productCategoryDTO, product.getPrice(), product.getCondition(),
          product.getStatus(), product.getDescription(), product.getSeller().getId(),
          product.getBuyer() != null ? product.getBuyer().getId() : null,
          product.getColor(), product.getProductionYear(), product.getCreatedAt());
      productGetResponseDTOList.add(productGetResponseDTO);
    }
  }

  /**
   * Deletes the given product based on UUID
   *
   * @param id UUID
   */
  public void deleteProductOrNull(UUID id) {
    Product product = productRepo.findById(id).orElse(null);
    assert product != null;
    productRepo.delete(product);
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
   * Finds product based on UUID id and seller UUID
   *
   * @param id     UUID of product
   * @param seller UUID of product
   * @return a product
   */
  public Product findProductByIdAndSeller(UUID id, Account seller) {
    return productRepo.findProductByIdAndSeller(id, seller).orElse(null);
  }

  public List<Product> getActiveListings(Account seller) {
    return productRepo.getActiveListingsHydrateProductCategoryAndBuyer(seller);
  }

  public List<Product> getSoldProducts(Account seller) {
    return productRepo.getSoldProducts(seller);
  }

}
