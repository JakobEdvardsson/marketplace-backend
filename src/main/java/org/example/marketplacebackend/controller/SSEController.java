package org.example.marketplacebackend.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.example.marketplacebackend.DTO.outgoing.productDTOs.ProductDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.MessageQueue;
import org.example.marketplacebackend.model.Product;
import org.example.marketplacebackend.repository.WatchListRepository;
import org.example.marketplacebackend.service.MessageQueueService;
import org.example.marketplacebackend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequestMapping("/v1/sse")
@CrossOrigin(origins = {
    "http://localhost:3000, https://marketplace.johros.dev"}, allowCredentials = "true")
@RestController
public class SSEController {

  private static final Logger log = LoggerFactory.getLogger(SSEController.class);
  private final Map<String, SseEmitter> online = Collections.synchronizedMap(new HashMap<>());
  private final WatchListRepository watchListRepository;
  private final MessageQueueService messageQueueService;
  private final UserService userService;

  public SSEController(WatchListRepository watchListRepository,
      MessageQueueService messageQueueService, UserService userService) {
    this.watchListRepository = watchListRepository;
    this.messageQueueService = messageQueueService;
    this.userService = userService;
  }

  @GetMapping(path = "/listen", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter listen(Principal principal) {
    Account user = userService.getAccountOrException(principal.getName());
    String userId = user.getId().toString();

    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

    emitter.onCompletion(() -> online.remove(userId));
    emitter.onTimeout(() -> online.remove(userId));
    emitter.onError(throwable -> {
      online.remove(userId);
      emitter.completeWithError(throwable);
    });

    online.put(userId, emitter);

    // deliver undelivered messages from message queue to the connecting client
    List<Product> productMessageQueue = getProductMessageQueue(user.getId());
    try {
      for (Product product : productMessageQueue) {
        ProductDTO productDTO = new ProductDTO(product.getName(),
            product.getProductCategory().getId(),
            product.getPrice(), product.getCondition(), product.getDescription(),
            product.getColor(),
            product.getProductionYear(), product.getId());
        emitter.send(SseEmitter.event().data(productDTO));
        messageQueueService.deleteByProductAndUser(product, user);
      }
    } catch (IOException e) {
      emitter.completeWithError(e);
    }

    return emitter;
  }

  public List<Product> getProductMessageQueue(UUID userId) {
    return messageQueueService.findUsersQueuedMessages(userId);
  }

  public void addToProductMessageQueue(String userId, Product product) {
    Account subscriber = new Account();
    subscriber.setId(UUID.fromString(userId));

    MessageQueue message = new MessageQueue();
    message.setProduct(product);
    message.setSubscriber(subscriber);

    messageQueueService.save(message);
  }

  @Async
  public void pushNewProduct(Product product) {
    ProductDTO productDTO = new ProductDTO(product.getName(), product.getProductCategory().getId(),
        product.getPrice(), product.getCondition(), product.getDescription(), product.getColor(),
        product.getProductionYear(), product.getId());

    List<String> subscriberIds = watchListRepository.findByProductCategory(
        product.getProductCategory());

    for (String subscriber : subscriberIds) {
      SseEmitter emitter = online.get(subscriber);
      if (emitter == null) {
        addToProductMessageQueue(subscriber, product);
        continue;
      }

      try {
        emitter.send(SseEmitter.event().data(productDTO));
      } catch (IOException e) {
        emitter.completeWithError(e);
        addToProductMessageQueue(subscriber, product);
      }
    }

  }

}
