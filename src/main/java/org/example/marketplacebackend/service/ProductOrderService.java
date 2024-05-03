package org.example.marketplacebackend.service;

import org.example.marketplacebackend.DTO.incoming.OrderItemDTO;
import org.example.marketplacebackend.model.OrderItem;
import org.example.marketplacebackend.model.ProductOrder;
import org.example.marketplacebackend.repository.OrderHistoryRepository;
import org.example.marketplacebackend.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductOrderService {
  private OrderHistoryRepository orderHistoryRepo;
  private OrderRepository orderItemRepo;
  public ProductOrderService(OrderHistoryRepository orderHistoryRepo, OrderRepository orderItemRepo) {
    this.orderItemRepo = orderItemRepo;
    this.orderHistoryRepo = orderHistoryRepo;
  }

  public ProductOrder saveOrder(ProductOrder productOrder) {
    return orderHistoryRepo.save(productOrder);
  }

  public List<OrderItem> saveOrderItems(List<OrderItemDTO> orderItems) {
    List<OrderItem> orderItemsDb = new ArrayList<>();
    for (OrderItemDTO productId: orderItems) {

    }
    return orderItemRepo.saveAll(orderItems);
  }

  public OrderItem getOrderOrNull(UUID orderId) {
    return orderItemRepo.findById(orderId).orElse(null);
  }
}
