package org.example.marketplacebackend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.example.marketplacebackend.repository.WatchListRepository;
import org.example.marketplacebackend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TestWatchlistController {
  @Container
  private static final PostgreSQLContainer<?> DB = new PostgreSQLContainer<>(
      "postgres:16-alpine"
  )
      .withInitScript("schema.sql");

  @DynamicPropertySource
  private static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", DB::getJdbcUrl);
    registry.add("spring.datasource.username", DB::getUsername);
    registry.add("spring.datasource.password", DB::getPassword);
  }



  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private WatchListRepository watchListRepository;

  @Autowired
  private UserService userService;

  @Test
  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, statements = {
      "INSERT INTO account (id, username, first_name, last_name, date_of_birth, email, password) VALUES ('c70a38f9-b770-4f2d-8c64-32cc583aac95', 'usernameInbox', 'firstnameInbox', 'lastnameInbox', '1990-01-01', 'inbox@example.com', '$2a$10$YltQfNKzHoF4Db1oUHtP/eODkthW90lPaouBw6Q1k/7keLcctilpm')",
      "INSERT INTO product_category (id, name) VALUES ('fdeb0281-1481-45f9-b005-e02bba579085', 'test category')",
      "INSERT INTO public.watchlist (product_category_id, subscriber_id, id) VALUES ('fdeb0281-1481-45f9-b005-e02bba579085', 'c70a38f9-b770-4f2d-8c64-32cc583aac95', '9ecff608-a7d9-4ff8-871d-3bf632ddef6d')"
  })
  @WithMockUser(username = "usernameInbox", roles = "USER")
  public void getCategoryList() throws Exception {

    ResultActions getWatchlist = mockMvc.perform(MockMvcRequestBuilders.get("/v1/watchlist")
        .principal(()-> "usernameInbox"));

    getWatchlist.andExpect(status().isOk());
  }
}