package org.example.marketplacebackend.repository;

import java.util.Optional;
import java.util.UUID;
import org.example.marketplacebackend.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, UUID> {

  Optional<Account> findByUsername(String username);

  Optional<Account> findById(UUID id);
  void deleteByUsername(String username);
}
