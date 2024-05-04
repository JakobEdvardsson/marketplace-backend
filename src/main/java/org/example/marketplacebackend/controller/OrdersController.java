package org.example.marketplacebackend.controller;

import org.example.marketplacebackend.DTO.incoming.OrderDTO;
import org.example.marketplacebackend.DTO.outgoing.OrderItemRegisteredResponseDTO;
import org.example.marketplacebackend.DTO.outgoing.OrderRegisteredResponseDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.OrderItem;
import org.example.marketplacebackend.model.Product;
import org.example.marketplacebackend.model.ProductOrder;
import org.example.marketplacebackend.repository.OrderHistoryRepository;
import org.example.marketplacebackend.service.ProductOrderService;
import org.example.marketplacebackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequestMapping("v1/orders")
@CrossOrigin(origins = {"localhost:3000",
    "https://marketplace.johros.dev"}, allowCredentials = "true")
@RestController
public class OrdersController {

  private final ProductOrderService productOrderService;
  private final UserService userService;
  private final OrderHistoryRepository orderHistoryRepository;

  public OrdersController(ProductOrderService productOrderService,
      UserService userService, OrderHistoryRepository orderHistoryRepository) {
    this.productOrderService = productOrderService;
    this.userService = userService;
    this.orderHistoryRepository = orderHistoryRepository;
  }

  @PostMapping("")
  public ResponseEntity<?> order(Principal principal, @RequestBody OrderDTO orderDTO) {
    String username = principal.getName();
    Account authenticatedUser = userService.getAccountOrException(username);

    ProductOrder order = new ProductOrder();
    order.setBuyer(authenticatedUser);
    order.setTimeOfPurchase(Instant.now());

    ProductOrder productOrder = productOrderService.save(order);

    List<OrderItem> orderItems = productOrderService.saveOrderItems(productOrder,
        orderDTO.orderItemDTOS());

    List<OrderItemRegisteredResponseDTO> orderItemRegisteredResponseDTOList = new ArrayList<>();
    for (OrderItem orderItem : orderItems) {
      OrderItemRegisteredResponseDTO orderRegisteredResponseDTO = new OrderItemRegisteredResponseDTO(
          orderItem.getProduct().getId(),
          orderItem.getProduct().getName(),
          orderItem.getProduct().getPrice()
      );
      orderItemRegisteredResponseDTOList.add(orderRegisteredResponseDTO);
    }

    OrderRegisteredResponseDTO response = new OrderRegisteredResponseDTO(productOrder.getId(),
        productOrder.getTimeOfPurchase(),
        orderItemRegisteredResponseDTOList);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("")
  public ResponseEntity<?> getAllOrders(Principal principal) {
    String username = principal.getName();
    Account authenticatedUser = userService.getAccountOrException(username);

    return ResponseEntity.status(HttpStatus.OK).body("");
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getOrderById(Principal principal) {
    String username = principal.getName();

    Account authenticatedUser = userService.getAccountOrException(username);
    return ResponseEntity.status(HttpStatus.OK).body("");
  }
}
