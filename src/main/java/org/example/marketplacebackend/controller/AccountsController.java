package org.example.marketplacebackend.controller;

import org.example.marketplacebackend.DTO.incoming.PasswordChangeDTO;
import org.example.marketplacebackend.DTO.incoming.UserDTO;
import org.example.marketplacebackend.DTO.outgoing.MyProfileResponseDTO;
import org.example.marketplacebackend.DTO.outgoing.ProfileResponseDTO;
import org.example.marketplacebackend.DTO.outgoing.UserRegisteredResponseDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.UUID;

@RequestMapping("v1/accounts")
@CrossOrigin(origins = {
    "http://localhost:3000, https://marketplace.johros.dev"}, allowCredentials = "true")
@Controller
public class AccountsController {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  public AccountsController(UserService userService, PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/password")
  public ResponseEntity<?> changeUserPassword(Principal principal, @RequestBody PasswordChangeDTO passwordChangeDTO) {
    Account authenticatedUser = userService.getAccountOrException(principal.getName());

    boolean successful = userService.passwordChange(authenticatedUser, passwordChangeDTO.oldPassword(), passwordChangeDTO.newPassword());
    if (!successful) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok().build();
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody UserDTO user) {
    Account userModel = new Account();
    String password = user.password();
    String encodedPassword = passwordEncoder.encode(password);

    userModel.setFirstName(user.firstName());
    userModel.setLastName(user.lastName());
    userModel.setUsername(user.username());
    userModel.setEmail(user.email());
    userModel.setPassword(encodedPassword);
    userModel.setDateOfBirth(user.dateOfBirth());

    Account userDB = userService.saveUser(userModel);

    UserRegisteredResponseDTO response = new UserRegisteredResponseDTO(
        userDB.getFirstName(), userDB.getLastName(),
        userDB.getEmail(), userDB.getUsername(), userDB.getDateOfBirth());

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getProfile(@PathVariable UUID id) {
    Account user = userService.getAccountOrNull(id);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    ProfileResponseDTO response = new ProfileResponseDTO(user.getFirstName(), user.getLastName(),
        user.getUsername());

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/me")
  public ResponseEntity<?> getMyProfile(Principal principal) {
    if (principal == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    Account authenticatedUser = userService.getAccountOrException(principal.getName());
    MyProfileResponseDTO response = new MyProfileResponseDTO(authenticatedUser.getFirstName(),
        authenticatedUser.getLastName(), authenticatedUser.getDateOfBirth(),
        authenticatedUser.getUsername(), authenticatedUser.getEmail());

    return ResponseEntity.status(HttpStatus.OK).body(response);
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