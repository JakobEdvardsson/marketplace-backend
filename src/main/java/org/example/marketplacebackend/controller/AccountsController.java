package org.example.marketplacebackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.marketplacebackend.DTO.incoming.UserDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.repository.AccountRepository;
import org.example.marketplacebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;

@RequestMapping("v1/accounts")
@CrossOrigin(origins = "localhost:3000", allowCredentials = "true")
@RestController
public class AccountsController {
  private final UserService userService;

  public AccountsController(UserService userService) {
    this.userService = userService;

  }

  @PostMapping ( "/register")
  public ResponseEntity<String> register(@RequestBody Account user, HttpServletRequest httpServletRequest) {
    userService.saveUser(user);
    return ResponseEntity.ok().body("Success");
  }

}
