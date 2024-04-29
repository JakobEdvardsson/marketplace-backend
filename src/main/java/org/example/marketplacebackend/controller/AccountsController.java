package org.example.marketplacebackend.controller;

import org.example.marketplacebackend.DTO.incoming.UserDTO;
import org.example.marketplacebackend.DTO.outgoing.UserRegisteredResponseDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

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
  public ResponseEntity<?> register(@RequestBody UserDTO user) {
    Account userModel = new Account();
    String password = user.password();
    String encodedPassword = passwordEncoder.encode(password);

    userModel.setFirst_name(user.firstName());
    userModel.setLast_name(user.lastName());
    userModel.setUsername(user.username());
    userModel.setEmail(user.email());
    userModel.setPassword(encodedPassword);
    userModel.setDate_of_birth(user.date_of_birth());

    Account userDB = userService.saveUser(userModel);

    UserRegisteredResponseDTO response = new UserRegisteredResponseDTO(
        userDB.getFirst_name(), userDB.getLast_name(),
        userDB.getEmail(), userDB.getUsername(), userDB.getDate_of_birth());

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("")
  public ResponseEntity<String> deleteUser(Principal principal) {
    String username = principal.getName();
    Account account = userService.getAccountOrNull(username);

    if (account != null) {
      userService.deleteUser(account);
      return ResponseEntity.status(HttpStatus.OK).body("Deleted successfully");
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
  }

}