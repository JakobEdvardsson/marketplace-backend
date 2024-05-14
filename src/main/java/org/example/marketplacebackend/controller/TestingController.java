package org.example.marketplacebackend.controller;

import java.security.Principal;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/v1/tests")
@CrossOrigin(origins = {"http://localhost:3000, https://marketplace.johros.dev"}, allowCredentials = "true")
@Controller
public class TestingController {

  private final UserService userService;

  public TestingController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/username")
  public ResponseEntity<?> getUsername(Principal principal) {
    Account authenticatedUser = userService.getAccountOrException(principal.getName());
    String json = "{\"username\": \"%s\"}".formatted(authenticatedUser.getUsername());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new ResponseEntity<>(json, headers, HttpStatus.OK);
  }

}
