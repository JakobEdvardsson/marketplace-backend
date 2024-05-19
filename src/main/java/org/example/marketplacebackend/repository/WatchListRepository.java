package org.example.marketplacebackend.repository;

import java.util.List;
import java.util.UUID;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.ProductCategory;
import org.example.marketplacebackend.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WatchListRepository extends JpaRepository<Watchlist, UUID> {

  List<Watchlist> findAllBySubscriber(Account subscriber);

  long deleteBySubscriberAndProductCategory(Account subscriber, ProductCategory productCategoryID);

  boolean existsBySubscriberAndProductCategoryId(Account subscriber, UUID productCategoryID);
  boolean existsBySubscriberAndProductCategoryName(Account subscriber, String productCategoryName);

  @Query("SELECT w.subscriber.id FROM Watchlist w WHERE w.productCategory = :category")
  List<String> findByProductCategory(ProductCategory category);
}