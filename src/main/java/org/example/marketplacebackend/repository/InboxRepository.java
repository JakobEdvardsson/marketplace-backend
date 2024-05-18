package org.example.marketplacebackend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.Inbox;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InboxRepository extends JpaRepository<Inbox, UUID> {

  @EntityGraph(attributePaths = {"product"})
  List<Inbox> findByReceiver(Account receiver);

  @EntityGraph(attributePaths = {"product"})
  Optional<Inbox> findByIdAndReceiver(UUID id, Account receiver);

  @EntityGraph(attributePaths = {"product"})
  Optional<Inbox> findByProductIdAndReceiver(UUID id, Account receiver);

  Long deleteByIdAndReceiver(UUID id, Account authenticatedUser);

}
