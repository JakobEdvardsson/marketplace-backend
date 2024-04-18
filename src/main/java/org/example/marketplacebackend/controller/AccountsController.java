package org.example.marketplacebackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RequestMapping("v1/accounts")
@CrossOrigin(origins = "localhost:3000", allowCredentials = "true")
@RestController
public class AccountsController {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  public AccountsController(UserService userService, PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody Account user) {
    String password = user.getPassword();
    String encodedPassword = passwordEncoder.encode(password);
    user.setPassword(encodedPassword);

    userService.saveUser(user);

    Account retrieveUser = userService.getAccountOrException(user.getId());
    ObjectMapper objectMapper = new ObjectMapper();
    String userJson;

    try {
      userJson = objectMapper.writeValueAsString(retrieveUser);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(userJson);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
    try {
      Account account = userService.getAccountOrException(id);
      userService.deleteUser(account);

      return ResponseEntity.status(HttpStatus.OK).body("Deleted successfully");
    } catch (UsernameNotFoundException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
    }
  }

}
