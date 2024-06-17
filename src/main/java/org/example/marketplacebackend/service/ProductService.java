package org.example.marketplacebackend.service;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.example.marketplacebackend.DTO.incoming.ProductCategoryDTO;
import org.example.marketplacebackend.DTO.outgoing.productDTOs.ProductGetAllResponseDTO;
import org.example.marketplacebackend.DTO.outgoing.productDTOs.ProductGetResponseDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.Product;
import org.example.marketplacebackend.model.ProductCategory;
import org.example.marketplacebackend.model.ProductStatus;
import org.example.marketplacebackend.repository.AccountRepository;
import org.example.marketplacebackend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

  private final ProductRepository productRepo;
  private final ProductImageService productImageService;
  private final AccountRepository accountRepo;

  public ProductService(ProductRepository productRepo, ProductImageService productImageService,
      AccountRepository accountRepo) {
    this.productRepo = productRepo;
    this.productImageService = productImageService;
    this.accountRepo = accountRepo;
  }

  @PersistenceContext
  private EntityManager entityManager;

  public ProductGetAllResponseDTO getProducts(String category, Integer minPrice, Integer maxPrice, Integer condition, Integer sort, String query) {
    EntityGraph<Product> entityGraph = entityManager.createEntityGraph(Product.class);

    entityGraph.addAttributeNodes("productCategory", "productImages", "buyer", "seller");
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Product> cq = cb.createQuery(Product.class);
    Root<Product> product = cq.from(Product.class);

    List<Predicate> predicates = new ArrayList<>();
    predicates.add(cb.equal(product.get("status"), ProductStatus.AVAILABLE.ordinal()));

    if (category != null) {
      predicates.add(cb.equal(product.get("productCategory").get("name"), category));
    }
    if (minPrice != null) {
      entityGraph.addAttributeNodes("price");
      predicates.add(cb.greaterThanOrEqualTo(product.get("price"), minPrice));
    }
    if (maxPrice != null) {
      predicates.add(cb.lessThanOrEqualTo(product.get("price"), maxPrice));
    }
    if (condition != null) {
      entityGraph.addAttributeNodes("condition");
      predicates.add(cb.equal(product.get("condition"), condition));
    }
    if (query != null) {
      predicates.add(cb.like(cb.lower(product.get("name")), "%" + query.toLowerCase() + "%"));
    }

    cq.where(predicates.toArray(new Predicate[0]));
    if (sort != null && sort == 1) {
      cq.orderBy(cb.asc(product.get("createdAt")));
    } else {
      cq.orderBy(cb.desc(product.get("createdAt")));
    }

    TypedQuery<Product> queryResult = entityManager.createQuery(cq);
    queryResult.setHint("jakarta.persistence.loadgraph", entityGraph);

    List<Product> products = queryResult.getResultList();

    List<ProductGetResponseDTO> productDTOS = new ArrayList<>();
    convertProductsToDTO(products, productDTOS);
    return new ProductGetAllResponseDTO(productDTOS);
  }

  /**
   * @param id The id of the product to be returned.
   * @return The product with the given id if it exists else null.
   */
  public Product getProductOrNull(UUID id) {
    return productRepo.findById(id).orElse(null);
  }

  public void setStatusSoldAndBuyerToDeleted(UUID id) {
    Account deleted = accountRepo.findByUsername("deleted").orElse(null);
    assert deleted != null;
    productRepo.updateProductByStatusAndBuyer(id, deleted);
  }

  public ProductGetAllResponseDTO getAllProductsByProvidedCategories(List<UUID> categories) {
    List<Product> products = productRepo.getAllProductsByProvidedCategories(categories);
    List<ProductGetResponseDTO> productGetResponseDTOList = new ArrayList<>();

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
          product.getColor(), product.getProductionYear(), product.getCreatedAt(),
          productImageService.productImagesToImageUrls(product.getProductImages())
      );
      productGetResponseDTOList.add(productGetResponseDTO);
    }
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
