package org.example.marketplacebackend.controller;

import org.example.marketplacebackend.DTO.outgoing.InboxGetAllResponseDTO;
import org.example.marketplacebackend.DTO.outgoing.WatchListResponseDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.Inbox;
import org.example.marketplacebackend.model.ProductCategory;
import org.example.marketplacebackend.model.Watchlist;
import org.example.marketplacebackend.repository.WatchListRepository;
import org.example.marketplacebackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestMapping("/v1/watchlist")
@CrossOrigin(origins = { "http://localhost:3000, https://marketplace.johros.dev" }, allowCredentials = "true")
@RestController
public class WatchlistController {
  private final UserService userService;
  private final WatchListRepository watchListRepository;

  public WatchlistController(UserService userService, WatchListRepository watchListRepository) {
    this.userService = userService;
    this.watchListRepository = watchListRepository;
  }

  @GetMapping("")
  public ResponseEntity<?> getWatchList(Principal user) {
    Account authenticatedUser = userService.getAccountOrException(user.getName());

    List<Watchlist> watchList = watchListRepository.findAllBySubscriber(authenticatedUser);

    if (watchList.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    List<WatchListResponseDTO> allWatchLists = watchList.stream().map(list -> new WatchListResponseDTO(list.getId(), list.getProductCategory())).toList();

    return ResponseEntity.status(HttpStatus.OK).body(allWatchLists);
  }

  @PostMapping("")
  public ResponseEntity<?> postWatchListItem(Principal user, @RequestBody ProductCategory productCategory) {

    if (productCategory == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    Account authenticatedUser = userService.getAccountOrException(user.getName());

    Watchlist watchList = new Watchlist();
    watchList.setSubscriber(authenticatedUser);
    watchList.setProductCategory(productCategory);

    Watchlist returnWatchList = watchListRepository.save(watchList);
    WatchListResponseDTO watchListResponseDTO = new WatchListResponseDTO(returnWatchList.getId(), returnWatchList.getProductCategory());

    return ResponseEntity.status(HttpStatus.OK).body(watchListResponseDTO);
  }
}