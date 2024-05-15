package org.example.marketplacebackend.repository;

import java.util.List;
import java.util.UUID;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.MessageQueue;
import org.example.marketplacebackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageQueueRepository extends JpaRepository<MessageQueue, UUID> {

  @Query("""
      SELECT m.product FROM MessageQueue m WHERE m.subscriber.id = :userId
      """)
  List<Product> findUsersQueuedMessages(UUID userId);

  void deleteByProductAndSubscriber(Product product, Account subscriber);
}
