package org.example.marketplacebackend.controller;

import org.example.marketplacebackend.DTO.incoming.OrderDTO;
import org.example.marketplacebackend.DTO.outgoing.OrderRegisteredResponseDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.ProductOrder;
import org.example.marketplacebackend.service.ProductOrderService;
import org.example.marketplacebackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;

@RequestMapping("v1/orders")
@CrossOrigin(origins = "localhost:3000", allowCredentials = "true")
@RestController
public class OrdersController {

  private final ProductOrderService productOrderService;
  private final UserService userService;

  public OrdersController(ProductOrderService productOrderService,
      UserService userService) {
    this.productOrderService = productOrderService;
    this.userService = userService;
  }

  @PostMapping("")
  public ResponseEntity<?> order(Principal principal, @RequestBody OrderDTO orderDTO) {
    String username = principal.getName();
    Account authenticatedUser = userService.getAccountOrException(username);

    ProductOrder order = new ProductOrder();
    order.setBuyer(authenticatedUser);

    ProductOrder productOrder = productOrderService.saveOrder(order);
    productOrderService.saveOrderItems(orderDTO.orderItems());

    OrderRegisteredResponseDTO response = new OrderRegisteredResponseDTO(productOrder);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
