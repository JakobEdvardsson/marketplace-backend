package org.example.marketplacebackend.testcontainers;

import java.util.UUID;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * NOTE: Docker must be running in the background for Testcontainer tests to run!
 * <p>
 * This class shows how to set up Testcontainers with a Postgres DB. The DB container instance will
 * be shared with all methods in the test class where it is set up, and then it will be nuked.
 * <p>
 * Please note that this DB setup has to be done in every test class that wishes to use
 * Testcontainers!
 */
@SpringBootTest
@Testcontainers
public class TestContainersWithClassScopeTests {

  // Account repo used as an example only. Any repo can be used in Testcontainer tests.
  @Autowired
  private AccountRepository accountRepository;

  // This sets up this class's database container
  @Container
  private static final PostgreSQLContainer<?> DB = new PostgreSQLContainer<>(
      "postgres:16-alpine"
  )
      .withInitScript("schema.sql"); // this runs an SQL script that creates the DB schema

  // This will configure Spring to use this class's DB container
  @DynamicPropertySource
  private static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", DB::getJdbcUrl);
    registry.add("spring.datasource.username", DB::getUsername);
    registry.add("spring.datasource.password", DB::getPassword);
  }

  // SQL statements can be run before method execution through script files or inline:
  // SQL script file usage:
  //@Sql(scripts = {"/script.sql"}) // place script files in "src/test/resources/" for this to work
  // Inline SQL usage (doesn't have to use text blocks):
  @Sql(statements = """
      INSERT INTO account (id, first_name, last_name, date_of_birth, email, password, username) VALUES ('6fd69dc3-69ca-69c5-420f-a7c420069e69', 'John', 'Doe', '2024-04-17', 'johndoe420@gmail.com', '$2a$10$WCKI2OPtafr0cYoTYxAuFuhG4I9TJ6HIVwk6oiUs8I75UaeLMbHvO', 'johndoe')
      """)
  @Test
  @Disabled
  public void getAccount_shouldBeAdded() {
    Account added = accountRepository.findById(
        UUID.fromString("6fd69dc3-69ca-69c5-420f-a7c420069e69")).orElseThrow();

    Assertions.assertNotNull(added);
  }

}
