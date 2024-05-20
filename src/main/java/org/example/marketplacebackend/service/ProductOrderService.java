package org.example.marketplacebackend.service;

import jakarta.annotation.Nullable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.example.marketplacebackend.DTO.incoming.OrderItemDTO;
import org.example.marketplacebackend.DTO.outgoing.orderDTOs.OrderItemRegisteredResponseDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.OrderItem;
import org.example.marketplacebackend.model.Product;
import org.example.marketplacebackend.model.ProductOrder;
import org.example.marketplacebackend.model.ProductStatus;
import org.example.marketplacebackend.repository.OrderHistoryRepository;
import org.example.marketplacebackend.repository.OrderRepository;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductOrderService {

  private final OrderHistoryRepository orderHistoryRepo;
  private final OrderRepository orderItemRepo;
  private final ProductService productService;

  public ProductOrderService(OrderHistoryRepository orderHistoryRepo,
      OrderRepository orderItemRepo, ProductService productService) {
    this.orderItemRepo = orderItemRepo;
    this.orderHistoryRepo = orderHistoryRepo;
    this.productService = productService;
  }

  public ProductOrder save(ProductOrder productOrder) {
    return orderHistoryRepo.save(productOrder);
  }

  public List<OrderItemRegisteredResponseDTO> saveOrderItems(Account authenticatedUser,
      ProductOrder order, List<OrderItemDTO> orderItems) {
    List<OrderItemRegisteredResponseDTO> orderItemsDTO = new ArrayList<>();

    for (OrderItemDTO orderItemDTO : orderItems) {
      OrderItem insert = new OrderItem();
      Product product = productService.getProductOrNull(orderItemDTO.productId());

      if (product == null) {
        continue;
      } else if (product.getStatus() == ProductStatus.SOLD.ordinal()
                 || product.getStatus() == ProductStatus.PENDING.ordinal()) {
        OrderItemRegisteredResponseDTO orderItemDTOError = new OrderItemRegisteredResponseDTO(
            product.getId(),
            product.getName(),
            product.getPrice(),
            true,
            product.getStatus()
        );
        orderItemsDTO.add(orderItemDTOError);
        continue;
      }

      product.setStatus(ProductStatus.PENDING.ordinal());
      product.setBuyer(authenticatedUser);
      productService.saveProduct(product);

      insert.setProduct(product);
      insert.setOrder(order);
      OrderItem saved = orderItemRepo.save(insert);
      OrderItemRegisteredResponseDTO orderItemDTOSuccess = new OrderItemRegisteredResponseDTO(
          saved.getProduct().getId(),
          saved.getProduct().getName(),
          saved.getProduct().getPrice(),
          false,
          product.getStatus()
      );

      orderItemsDTO.add(orderItemDTOSuccess);
    }

    return orderItemsDTO;
  }

  public @Nullable OrderItem getOrderItemOrNull(UUID orderId) {
    return orderItemRepo.findById(orderId).orElse(null);
  }

  public @Nullable ProductOrder getOrderOrNull(UUID orderId) {
    return orderHistoryRepo.findById(orderId).orElse(null);
  }

  public List<ProductOrder> getAllOrders(UUID buyerId) {
    return orderHistoryRepo.findAllByBuyer_Id(buyerId);
  }

  public List<ProductOrder> getAllOrdersByPeriod(UUID buyerId, Instant start, Instant end) {
    return orderHistoryRepo.findAllOrdersByPeriod(buyerId, start, end);
  }

  public ProductOrder getProductOrderByBuyerIdAndId(UUID buyerId, UUID id) {
    return orderHistoryRepo.getProductOrderByBuyer_IdAndId(buyerId, id).orElse(null);
  }

  public Optional<OrderItem> getOrderForProduct(Product product) {
    return orderItemRepo.findByProduct(product);
  }

  public void deleteOrderItem(OrderItem orderItem) {
    orderItemRepo.delete(orderItem);
  }
}
