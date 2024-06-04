package org.example.marketplacebackend.controller;

import org.example.marketplacebackend.DTO.incoming.OrderDTO;
import org.example.marketplacebackend.DTO.incoming.StatusDTO;
import org.example.marketplacebackend.DTO.outgoing.orderDTOs.OrderGetAllResponseDTO;
import org.example.marketplacebackend.DTO.outgoing.orderDTOs.OrderGetResponseDTO;
import org.example.marketplacebackend.DTO.outgoing.orderDTOs.OrderItemRegisteredResponseDTO;
import org.example.marketplacebackend.DTO.outgoing.orderDTOs.OrderRegisteredResponseDTO;
import org.example.marketplacebackend.DTO.outgoing.orderDTOs.OrderStatusResponseDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.OrderItem;
import org.example.marketplacebackend.model.Product;
import org.example.marketplacebackend.model.ProductOrder;
import org.example.marketplacebackend.model.ProductStatus;
import org.example.marketplacebackend.service.ProductOrderService;
import org.example.marketplacebackend.service.ProductService;
import org.example.marketplacebackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequestMapping("v1/orders")
@CrossOrigin(origins = {"localhost:3000",
    "https://marketplace.johros.dev"}, allowCredentials = "true")
@RestController
public class OrdersController {

  private final ProductOrderService productOrderService;
  private final UserService userService;
  private final ProductService productService;

  public OrdersController(ProductOrderService productOrderService,
      UserService userService, ProductService productService) {
    this.productOrderService = productOrderService;
    this.userService = userService;
    this.productService = productService;
  }

  @PostMapping("")
  public ResponseEntity<?> order(Principal principal, @RequestBody OrderDTO orderDTO) {
    String username = principal.getName();
    Account authenticatedUser = userService.getAccountOrException(username);

    ProductOrder dbInsertOrder = new ProductOrder();
    dbInsertOrder.setBuyer(authenticatedUser);

    ProductOrder productOrder = productOrderService.save(dbInsertOrder);

    List<OrderItemRegisteredResponseDTO> orderItemsDTO = productOrderService.saveOrderItems(
        authenticatedUser, productOrder,
        orderDTO.orderItemDTOS());

    OrderRegisteredResponseDTO response = new OrderRegisteredResponseDTO(
        productOrder.getId(),
        productOrder.getTimeOfPurchase(),
        orderItemsDTO
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("")
  public ResponseEntity<?> getBuyOrders(Principal principal,
      @RequestParam(required = false) Instant start, @RequestParam(required = false) Instant end) {
    String username = principal.getName();
    Account authenticatedUser = userService.getAccountOrException(username);
    UUID buyerId = authenticatedUser.getId();
    List<ProductOrder> orders;

    if (start == null || end == null) {
      orders = productOrderService.getAllOrders(buyerId);
    } else {
      orders = productOrderService.getAllOrdersByPeriod(buyerId, start, end);
    }

    if (orders.isEmpty()) {
      return ResponseEntity.status(HttpStatus.OK).build();
    }

    List<OrderRegisteredResponseDTO> ordersDTO = new ArrayList<>();
    // select
    for (ProductOrder productOrder : orders) {
      List<OrderItem> orderItems = productOrder.getOrderItems();
      List<OrderItemRegisteredResponseDTO> orderItemsDTO = new ArrayList<>();

      for (OrderItem orderItem : orderItems) {
        OrderItemRegisteredResponseDTO orderItemDTO = new OrderItemRegisteredResponseDTO(
            orderItem.getOrder().getId(),
            orderItem.getProduct().getName(),
            orderItem.getProduct().getPrice(),
            false,
            orderItem.getProduct().getStatus()
        );
        orderItemsDTO.add(orderItemDTO);
      }

      OrderRegisteredResponseDTO orderDTO = new OrderRegisteredResponseDTO(productOrder.getId(),
          productOrder.getTimeOfPurchase(), orderItemsDTO);
      ordersDTO.add(orderDTO);
    }

    OrderGetAllResponseDTO response = new OrderGetAllResponseDTO(ordersDTO);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PatchMapping("/{productId}")
  public ResponseEntity<?> setOrderStatus(Principal principal, @PathVariable UUID productId,
      @RequestBody
      StatusDTO statusDTO) {
    String username = principal.getName();

    Account authenticatedUser = userService.getAccountOrException(username);
    Product product = productService.findProductByIdAndSeller(productId, authenticatedUser);

    if (product == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    if (product.getBuyer() == null || product.getStatus() != ProductStatus.PENDING.ordinal()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    if (statusDTO.accept()) {
      product.setStatus(ProductStatus.SOLD.ordinal());
      productService.saveProduct(product);
      OrderStatusResponseDTO response = new OrderStatusResponseDTO(ProductStatus.SOLD.ordinal());
      return ResponseEntity.status(HttpStatus.OK).body(response);

    } else {
      productOrderService.getOrderForProduct(product)
          .ifPresent(productOrderService::deleteOrderItem);

      product.setStatus(ProductStatus.AVAILABLE.ordinal());
      product.setBuyer(null);
      productService.saveProduct(product);
      OrderStatusResponseDTO response = new OrderStatusResponseDTO(
          ProductStatus.AVAILABLE.ordinal());
      return ResponseEntity.status(HttpStatus.OK).body(response);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getOrderById(Principal principal, @PathVariable UUID id) {
    String username = principal.getName();

    Account authenticatedUser = userService.getAccountOrException(username);

    ProductOrder order = productOrderService.getProductOrderByBuyerIdAndId(
        authenticatedUser.getId(), id);

    if (order == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    List<OrderItem> orderItems = order.getOrderItems();

    List<OrderItemRegisteredResponseDTO> orderItemsDTO = new ArrayList<>();
    for (OrderItem item : orderItems) {
      OrderItemRegisteredResponseDTO orderItemDTO = new OrderItemRegisteredResponseDTO(
          item.getProduct().getId(),
          item.getProduct().getName(),
          item.getProduct().getPrice(),
          false,
          item.getProduct().getStatus()
      );
      orderItemsDTO.add(orderItemDTO);
    }

    OrderGetResponseDTO response = new OrderGetResponseDTO(
        order.getId(),
        order.getTimeOfPurchase(),
        orderItemsDTO
    );
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

}
