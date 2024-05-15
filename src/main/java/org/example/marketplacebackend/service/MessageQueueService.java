package org.example.marketplacebackend.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.MessageQueue;
import org.example.marketplacebackend.model.Product;
import org.example.marketplacebackend.repository.MessageQueueRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageQueueService {

  private final MessageQueueRepository messageQueueRepository;

  public MessageQueueService(MessageQueueRepository messageQueueRepository) {
    this.messageQueueRepository = messageQueueRepository;
  }

  public void save(@NonNull MessageQueue messageQueue) {
    messageQueueRepository.save(messageQueue);
  }

  public List<Product> findUsersQueuedMessages(UUID userId) {
    return messageQueueRepository.findUsersQueuedMessages(userId);
  }

  @Transactional
  public void deleteByProductAndUser(Product product, Account subscriber) {
    messageQueueRepository.deleteByProductAndSubscriber(product, subscriber);
  }
}
