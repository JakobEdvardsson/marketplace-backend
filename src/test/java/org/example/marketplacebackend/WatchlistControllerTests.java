package org.example.marketplacebackend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.example.marketplacebackend.model.ProductCategory;
import org.example.marketplacebackend.repository.ProductCategoryRepository;
import org.example.marketplacebackend.repository.WatchListRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
class WatchlistControllerTests {

  @Container
  private static final PostgreSQLContainer<?> DB = new PostgreSQLContainer<>(
      "postgres:16-alpine"
  )
      .withInitScript("schema.sql");
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private WatchListRepository watchListRepository;
  @Autowired
  private ProductCategoryRepository productCategoryRepository;

  @DynamicPropertySource
  private static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", DB::getJdbcUrl);
    registry.add("spring.datasource.username", DB::getUsername);
    registry.add("spring.datasource.password", DB::getPassword);
  }

  //#################
  //GET v1/watchlist#
  //#################
  @Test
  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, statements = {
      "INSERT INTO account (id, username, first_name, last_name, date_of_birth, email, password) VALUES ('c70a38f9-b770-4f2d-8c64-32cc583aac95', 'usernameInbox', 'firstnameInbox', 'lastnameInbox', '1990-01-01', 'inbox@example.com', '$2a$10$YltQfNKzHoF4Db1oUHtP/eODkthW90lPaouBw6Q1k/7keLcctilpm')",
      "INSERT INTO product_category (id, name) VALUES ('fdeb0281-1481-45f9-b005-e02bba579085', 'test category')",
      "INSERT INTO public.watchlist (product_category_id, subscriber_id, id) VALUES ('fdeb0281-1481-45f9-b005-e02bba579085', 'c70a38f9-b770-4f2d-8c64-32cc583aac95', '9ecff608-a7d9-4ff8-871d-3bf632ddef6d')"
  })
  @Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = {
      "DELETE FROM watchlist WHERE product_category_id = 'fdeb0281-1481-45f9-b005-e02bba579085'",
      "DELETE FROM product_category WHERE id = 'fdeb0281-1481-45f9-b005-e02bba579085'",
      "DELETE FROM account WHERE id = 'c70a38f9-b770-4f2d-8c64-32cc583aac95'"
  })
  @WithMockUser(username = "usernameInbox", roles = "USER")
  public void getWatchlist() throws Exception {

    ResultActions getWatchlist = mockMvc.perform(MockMvcRequestBuilders.get("/v1/watchlist")
        .principal(() -> "usernameInbox"));

    getWatchlist.andExpect(status().isOk());
  }

  //##################
  //POST v1/watchlist#
  //##################
  @Test
  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, statements = {
      "INSERT INTO account (id, username, first_name, last_name, date_of_birth, email, password) VALUES ('c70a38f9-b770-4f2d-8c64-32cc583aac95', 'usernameInbox', 'firstnameInbox', 'lastnameInbox', '1990-01-01', 'inbox@example.com', '$2a$10$YltQfNKzHoF4Db1oUHtP/eODkthW90lPaouBw6Q1k/7keLcctilpm')",
      "INSERT INTO product_category (id, name) VALUES ('fdeb0281-1481-45f9-b005-e02bba579085', 'test category')"
  })
  @Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = {
      "DELETE FROM watchlist WHERE product_category_id = 'fdeb0281-1481-45f9-b005-e02bba579085'",
      "DELETE FROM product_category WHERE id = 'fdeb0281-1481-45f9-b005-e02bba579085'",
      "DELETE FROM account WHERE id = 'c70a38f9-b770-4f2d-8c64-32cc583aac95'"
  })
  @WithMockUser(username = "usernameInbox", roles = "USER")
  @Disabled
  public void postWatchListItem() throws Exception {

    ProductCategory productCategory = productCategoryRepository.findById(
        UUID.fromString("fdeb0281-1481-45f9-b005-e02bba579085")).orElseThrow();

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(productCategory.getId());

    ResultActions resultActions = mockMvc.perform(post("/v1/watchlist")
        .principal(() -> "usernameInbox")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json));

    resultActions.andExpect(status().isOk());

    resultActions = mockMvc.perform(post("/v1/watchlist")
        .principal(() -> "usernameInbox")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json));

    resultActions.andExpect(status().isBadRequest());
  }

  //####################
  //DELETE v1/watchlist#
  //####################
  @Test
  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, statements = {
      "INSERT INTO account (id, username, first_name, last_name, date_of_birth, email, password) VALUES ('c70a38f9-b770-4f2d-8c64-32cc583aac95', 'usernameInbox', 'firstnameInbox', 'lastnameInbox', '1990-01-01', 'inbox@example.com', '$2a$10$YltQfNKzHoF4Db1oUHtP/eODkthW90lPaouBw6Q1k/7keLcctilpm')",
      "INSERT INTO product_category (id, name) VALUES ('fdeb0281-1481-45f9-b005-e02bba579085', 'test category')",
      "INSERT INTO public.watchlist (product_category_id, subscriber_id, id) VALUES ('fdeb0281-1481-45f9-b005-e02bba579085', 'c70a38f9-b770-4f2d-8c64-32cc583aac95', '9ecff608-a7d9-4ff8-871d-3bf632ddef6d')"
  })
  @Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = {
      "DELETE FROM product_category WHERE id = 'fdeb0281-1481-45f9-b005-e02bba579085'",
      "DELETE FROM account WHERE id = 'c70a38f9-b770-4f2d-8c64-32cc583aac95'"
  })
  @WithMockUser(username = "usernameInbox", roles = "USER")
  public void deleteWatchListItem() throws Exception {

    ResultActions resultActions = mockMvc.perform(
        delete("/v1/watchlist/fdeb0281-1481-45f9-b005-e02bba579085")
            .principal(() -> "usernameInbox")
            .contentType(MediaType.APPLICATION_JSON));

    resultActions.andExpect(status().isOk());
    Assertions.assertTrue(
        watchListRepository.findById(UUID.fromString("9ecff608-a7d9-4ff8-871d-3bf632ddef6d"))
            .isEmpty());
  }
}