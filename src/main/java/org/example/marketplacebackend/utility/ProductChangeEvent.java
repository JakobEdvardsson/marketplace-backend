package org.example.marketplacebackend.utility;

import org.example.marketplacebackend.DTO.incoming.ProductDTO;
import org.example.marketplacebackend.model.Product;
import org.springframework.context.ApplicationEvent;

public class ProductChangeEvent extends ApplicationEvent {
  private final ProductDTO product;
  public ProductChangeEvent(Object source, ProductDTO product) {
    super(source);
    this.product = product;
  }

  public ProductDTO getEntity() {
    return product;
  }
}
