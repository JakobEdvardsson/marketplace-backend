package org.example.marketplacebackend.controller;

import org.example.marketplacebackend.repository.AccountRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;

@RequestMapping("v1/account")
@CrossOrigin(origins = "localhost:3000", allowCredentials = "true")
@Controller
public class AccountsController {
  private final AccountRepository accountRepository;

  public AccountsController (AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @GetMapping("/test")
  public Map<String, Object> greeting() {
    return Collections.singletonMap("message", "Hello, World");
  }

}
