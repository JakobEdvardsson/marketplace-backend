package org.example.marketplacebackend.controller;

import java.util.List;
import org.example.marketplacebackend.model.ProductCategory;
import org.example.marketplacebackend.repository.ProductCategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("v1/categories")
@CrossOrigin(origins = {"http://localhost:3000, https://marketplace.johros.dev"}, allowCredentials = "true")
@Controller
public class CategoriesController {

  private final ProductCategoryRepository productCategoryRepository;

  public CategoriesController(ProductCategoryRepository productCategoryRepository) {
    this.productCategoryRepository = productCategoryRepository;
  }

  @GetMapping("")
  public ResponseEntity<List<ProductCategory>> getCategories() {
    List<ProductCategory> categories = productCategoryRepository.findAll();
    if (categories.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok().body(categories);
  }

}
