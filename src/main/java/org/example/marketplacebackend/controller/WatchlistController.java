package org.example.marketplacebackend.controller;

import jakarta.transaction.Transactional;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import org.example.marketplacebackend.DTO.incoming.ProductCategoryDTO;
import org.example.marketplacebackend.DTO.outgoing.WatchListResponseDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.ProductCategory;
import org.example.marketplacebackend.model.Watchlist;
import org.example.marketplacebackend.repository.ProductCategoryRepository;
import org.example.marketplacebackend.repository.WatchListRepository;
import org.example.marketplacebackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/watchlist")
@CrossOrigin(origins = {
    "http://localhost:3000, https://marketplace.johros.dev"}, allowCredentials = "true")
@RestController
public class WatchlistController {

  private final UserService userService;
  private final WatchListRepository watchListRepository;
  private final ProductCategoryRepository productCategoryRepository;

  public WatchlistController(UserService userService, WatchListRepository watchListRepository,
      ProductCategoryRepository productCategoryRepository) {
    this.userService = userService;
    this.watchListRepository = watchListRepository;
    this.productCategoryRepository = productCategoryRepository;
  }

  @GetMapping("")
  public ResponseEntity<?> getWatchList(Principal user) {
    Account authenticatedUser = userService.getAccountOrException(user.getName());

    List<Watchlist> watchList = watchListRepository.findAllBySubscriber(authenticatedUser);

    List<WatchListResponseDTO> allWatchLists = watchList.stream()
        .map(list -> new WatchListResponseDTO(list.getId(), new ProductCategoryDTO(list.getProductCategory().getId(), list.getProductCategory().getName()))).toList();

    return ResponseEntity.status(HttpStatus.OK).body(allWatchLists);
  }

  @PostMapping("")
  public ResponseEntity<?> postWatchListItem(Principal user,
      @RequestBody String productCategoryName) {

    if (productCategoryName == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    ProductCategory productCategory = productCategoryRepository.findByName(productCategoryName)
        .orElse(null);

    if (productCategory == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    Account authenticatedUser = userService.getAccountOrException(user.getName());
    if (watchListRepository.existsBySubscriberAndProductCategoryName(authenticatedUser,
        productCategoryName)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    Watchlist watchList = new Watchlist();
    watchList.setSubscriber(authenticatedUser);

    watchList.setProductCategory(productCategory);

    Watchlist returnWatchList = watchListRepository.save(watchList);
    WatchListResponseDTO watchListResponseDTO = new WatchListResponseDTO(returnWatchList.getId(),
        new ProductCategoryDTO(returnWatchList.getProductCategory().getId(),
            returnWatchList.getProductCategory().getName()));

    return ResponseEntity.status(HttpStatus.OK).body(watchListResponseDTO);
  }

  @Transactional
  @DeleteMapping("/{productCategoryID}")
  public ResponseEntity<?> deleteWatchListItem(Principal user,
      @PathVariable UUID productCategoryID) {
    if (productCategoryID == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    Account authenticatedUser = userService.getAccountOrException(user.getName());

    ProductCategory productCategory = productCategoryRepository.findById(productCategoryID)
        .orElse(null);
    if (productCategory == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    if (watchListRepository.deleteBySubscriberAndProductCategory(authenticatedUser, productCategory)
        < 1) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    return ResponseEntity.status(HttpStatus.OK).build();
  }
}