package org.example.marketplacebackend;

import java.util.UUID;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class TestContainersWithClassScopeTests {

  @Container
  private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      "postgres:16-alpine"
  );

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  private AccountRepository accountRepository;

  @Sql(scripts = {"/data-testcontainers-test.sql"})
  @Test
  public void testAddAccount() {
    Account account = accountRepository.findById(
        UUID.fromString("8fd54dc3-04ca-41c5-878f-a7c355178e32")).orElseThrow();

    Assertions.assertNotNull(account);
  }

  @Test
  public void testAccountPersisted() {
    Account account = accountRepository.findById(
        UUID.fromString("8fd54dc3-04ca-41c5-878f-a7c355178e32")).orElseThrow();

    Assertions.assertNotNull(account);
  }

}
