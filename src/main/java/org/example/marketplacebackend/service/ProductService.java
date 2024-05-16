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

  public ProductGetAllResponseDTO getAllByProductPrice(Integer minPrice, Integer maxPrice) {
    List<Product> products = productRepo.getProductsByPrice(minPrice, maxPrice);
    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();

    convertProductsToDTO(products, productGetResponseDTOList);

    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllByProductPriceAndSort(Integer minPrice, Integer maxPrice,
      Integer sort) {
    List<Product> products;
    if (sort == 0) {
      products = productRepo.getProductsByPriceAndAsc(minPrice, maxPrice);
    } else {
      products = productRepo.getProductsByPriceAndDesc(minPrice, maxPrice);
    }

    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();
    convertProductsToDTO(products, productGetResponseDTOList);
    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllByProductPriceAndCategory(String category, Integer minPrice,
      Integer maxPrice) {
    List<Product> products = productRepo.getProductsByPriceAndCategory(category, minPrice,
        maxPrice);
    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();

    convertProductsToDTO(products, productGetResponseDTOList);

    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllByProductPriceAndCategoryAndSort(String category,
      Integer minPrice, Integer maxPrice, Integer sort) {
    List<Product> products;
    if (sort == 0) {
      products = productRepo.getProductsByPriceAndCategoryASC(category, minPrice, maxPrice);
    } else {
      products = productRepo.getProductsByPriceAndCategoryDESC(category, minPrice, maxPrice);
    }
    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();

    convertProductsToDTO(products, productGetResponseDTOList);

    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllByProductCategory(ProductCategory category) {
    List<Product> products = productRepo.getAllByProductCategory(category);
    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();

    convertProductsToDTO(products, productGetResponseDTOList);

    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllByProductCategoryAndSort(ProductCategory productCategory,
      Integer sort) {
    List<Product> products;
    if (sort == 0) {
      products = productRepo.getAllByProductCategoryAndAsc(productCategory);
    } else {
      products = productRepo.getAllByProductCategoryAndDesc(productCategory);
    }

    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();
    convertProductsToDTO(products, productGetResponseDTOList);
    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllByCondition(Integer condition) {
    List<Product> products = productRepo.getProductsByCondition(condition);
    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();

    convertProductsToDTO(products, productGetResponseDTOList);

    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllBySort(Integer sort) {
    List<Product> products;
    if (sort == 0) {
      products = productRepo.getProductsByAsc();
    } else {
      products = productRepo.getProductsByDesc();
    }
    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();

    convertProductsToDTO(products, productGetResponseDTOList);

    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllByConditionAndSort(Integer condition, Integer sort) {
    List<Product> products;
    if (sort == 0) {
      products = productRepo.getProductsByConditionAndAsc(condition);
    } else {
      products = productRepo.getProductsByConditionAndDesc(condition);
    }

    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();
    convertProductsToDTO(products, productGetResponseDTOList);
    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllByConditionAndCategory(Integer condition, String category) {
    List<Product> products = productRepo.getProductsByConditionAndCategory(condition, category);
    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();

    convertProductsToDTO(products, productGetResponseDTOList);

    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllByConditionAndCategoryAndSort(Integer condition,
      String category, Integer sort) {
    List<Product> products;
    if (sort == 0) {
      products = productRepo.getProductsByConditionAndCategoryAndAsc(condition, category);
    } else {
      products = productRepo.getProductsByConditionAndCategoryAndDesc(condition, category);
    }

    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();
    convertProductsToDTO(products, productGetResponseDTOList);
    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllByConditionAndPrice(Integer condition, Integer minPrice,
      Integer maxPrice) {
    List<Product> products = productRepo.getProductsByConditionAndPrice(condition, minPrice,
        maxPrice);
    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();

    convertProductsToDTO(products, productGetResponseDTOList);

    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllByConditionAndPriceAndSort(Integer condition,
      Integer minPrice, Integer maxPrice, Integer sort) {
    List<Product> products;
    if (sort == 0) {
      products = productRepo.getProductsByConditionAndPriceAndAsc(condition, minPrice,
          maxPrice);
    } else {
      products = productRepo.getProductsByConditionAndPriceAndDesc(condition, minPrice,
          maxPrice);
    }
    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();

    convertProductsToDTO(products, productGetResponseDTOList);

    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllByConditionAndCategoryAndPrice(Integer condition,
      String category, Integer minPrice, Integer maxPrice) {
    List<Product> products = productRepo.getProductsByConditionAndCategoryAndPrice(condition,
        category, minPrice, maxPrice);
    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();

    convertProductsToDTO(products, productGetResponseDTOList);

    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }


  public ProductGetAllResponseDTO getAllByConditionAndCategoryAndPriceAndSort(Integer condition,
      String category, Integer minPrice, Integer maxPrice,
      Integer sort) {
    List<Product> products;
    if (sort == 0) {
      products = productRepo.getProductsByConditionAndCategoryAndPriceAndAsc(condition,
          category, minPrice, maxPrice);
    } else {
      products = productRepo.getProductsByConditionAndCategoryAndPriceAndDesc(condition,
          category, minPrice, maxPrice);
    }

    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();
    convertProductsToDTO(products, productGetResponseDTOList);
    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllByMinPrice(Integer minPrice) {
    List<Product> products = productRepo.getProductsByMinPrice(minPrice);
    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();

    convertProductsToDTO(products, productGetResponseDTOList);

    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllByMaxPrice(Integer maxPrice) {
    List<Product> products = productRepo.getProductsByMaxPrice(maxPrice);
    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();

    convertProductsToDTO(products, productGetResponseDTOList);

    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllByMinPriceAndSort(Integer minPrice, Integer sort) {
    List<Product> products;
    if (sort == 0) {
      products = productRepo.getAllByMinPriceAndSortAsc(minPrice);
    } else {
      products = productRepo.getAllByMinPriceAndSortDesc(minPrice);
    }

    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();
    convertProductsToDTO(products, productGetResponseDTOList);

    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }
  public ProductGetAllResponseDTO getAllByProductMinPriceAndConditionAndSort(Integer minPrice, Integer condition, Integer sort) {
    List<Product> products;
    if (sort == 0) {
      products = productRepo.getAllByMinPriceAndConditionAndSortAsc(minPrice, condition);
    } else {
      products = productRepo.getAllByMinPriceAndConditionAndSortDesc(minPrice, condition);
    }

    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();
    convertProductsToDTO(products, productGetResponseDTOList);

    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllByProductMaxPriceAndConditionAndSort(Integer maxPrice, Integer condition, Integer sort) {
    List<Product> products;
    if (sort == 0) {
      products = productRepo.getAllByMaxPriceAndConditionAndSortAsc(maxPrice, condition);
    } else {
      products = productRepo.getAllByMaxPriceAndConditionAndSortDesc(maxPrice, condition);
    }

    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();
    convertProductsToDTO(products, productGetResponseDTOList);

    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllByProductMinPriceAndCondition(Integer minPrice, Integer condition) {
    List<Product> products = productRepo.getAllByMinPriceAndCondition(minPrice, condition);

    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();
    convertProductsToDTO(products, productGetResponseDTOList);

    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllByProductMaxPriceAndCondition(Integer maxPrice, Integer condition) {
    List<Product> products = productRepo.getAllByMaxPriceAndCondition(maxPrice, condition);

    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();
    convertProductsToDTO(products, productGetResponseDTOList);

    return new ProductGetAllResponseDTO(
        productGetResponseDTOList);
  }

  public ProductGetAllResponseDTO getAllByMaxPriceAndSort(Integer maxPrice, Integer sort) {
    List<Product> products;
    if (sort == 0) {
      products = productRepo.getAllByMaxPriceAndSortAsc(maxPrice);
    } else {
      products = productRepo.getAllByMaxPriceAndSortDesc(maxPrice);
    }

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
