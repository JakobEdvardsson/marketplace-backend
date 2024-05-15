package org.example.marketplacebackend.listeners;

import jakarta.persistence.PostPersist;
import org.example.marketplacebackend.DTO.incoming.ProductDTO;
import org.example.marketplacebackend.model.Product;
import org.example.marketplacebackend.utility.ProductChangeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ProductListener {

  private static ApplicationEventPublisher publisher;

  @Autowired
  public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
    ProductListener.publisher = publisher;
  }

  @PostPersist
  public void afterAnyInsert(Product product) {
    System.out.println("Publishing a product event");
    ProductDTO productDTO = new ProductDTO(product.getName(),
        product.getProductCategory().getId(), product.getPrice(),
        product.getCondition(), product.getDescription(), product.getColor(),
        product.getProductionYear());
    if (publisher != null) {
      ProductChangeEvent event = new ProductChangeEvent(this, productDTO);
      publisher.publishEvent(event);
    }
  }
}
