package org.example.marketplacebackend.repository;

import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.ProductCategory;
import org.example.marketplacebackend.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface WatchListRepository extends JpaRepository<Watchlist, UUID> {
  List<Watchlist> findAllBySubscriber(Account subscriber);
  void deleteBySubscriberAndProductCategory(Account subscriber, ProductCategory productCategoryID);
}