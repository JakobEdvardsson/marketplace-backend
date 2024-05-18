package org.example.marketplacebackend;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.example.marketplacebackend.DTO.outgoing.InboxGetAllResponseDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.Inbox;
import org.example.marketplacebackend.repository.InboxRepository;
import org.example.marketplacebackend.service.UserService;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@EnableWebMvc
public class InboxEndpointsTests {

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
  private UserService userService;

  @Autowired
  private InboxRepository inboxRepository;

  //#############
  //GET v1/inbox#
  //#############
  @WithMockUser(username = "usernameInbox", roles = "USER")
  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, statements = {
      "INSERT INTO account (id, username, first_name, last_name, date_of_birth, email, password) VALUES ('c70a38f9-b770-4f2d-8c64-32cc583aac95', 'usernameInbox', 'firstnameInbox', 'lastnameInbox', '1990-01-01', 'inbox@example.com', '$2a$10$YltQfNKzHoF4Db1oUHtP/eODkthW90lPaouBw6Q1k/7keLcctilpm')",
      "INSERT INTO product_category VALUES ('d5509745-450f-4760-8bdd-ddc88d376b37', 'electronics')",
      "INSERT INTO product(id, name, product_category, price, condition, status, description, seller, buyer, color, production_year, created_at) VALUES ('e8fc64de-5a16-4b76-bac0-cf4a20052589', 'test', 'd5509745-450f-4760-8bdd-ddc88d376b37', 200, 0, 0, 'wow', 'c70a38f9-b770-4f2d-8c64-32cc583aac95', null, 0, 2019, now())",
      "INSERT INTO inbox (id, receiver_id, message, is_read, sent_at, product_id) VALUES ('a2bb9999-894d-4afc-9f7d-b104be716347','c70a38f9-b770-4f2d-8c64-32cc583aac95', 'Test message 1', false, now(), 'e8fc64de-5a16-4b76-bac0-cf4a20052589')",
      "INSERT INTO inbox (id, receiver_id, message, is_read, sent_at, product_id) VALUES ('b2bb9999-894d-4afc-9f7d-b104be716347','c70a38f9-b770-4f2d-8c64-32cc583aac95', 'Test message 2', false, now(), 'e8fc64de-5a16-4b76-bac0-cf4a20052589')",
      "INSERT INTO inbox (id, receiver_id, message, is_read, sent_at, product_id) VALUES ('c2bb9999-894d-4afc-9f7d-b104be716347','c70a38f9-b770-4f2d-8c64-32cc583aac95', 'Test message 3', false, now(), 'e8fc64de-5a16-4b76-bac0-cf4a20052589')"
  })
  @Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = {
      "DELETE FROM inbox WHERE id = 'a2bb9999-894d-4afc-9f7d-b104be716347'",
      "DELETE FROM inbox WHERE id = 'b2bb9999-894d-4afc-9f7d-b104be716347'",
      "DELETE FROM inbox WHERE id = 'c2bb9999-894d-4afc-9f7d-b104be716347'",
      "DELETE FROM account WHERE id = 'c70a38f9-b770-4f2d-8c64-32cc583aac95'",
      "DELETE from product",
      "DELETE FROM product_category",

  })

  @Test
  public void getAllMessages() throws Exception {
    Account account = userService.getAccountOrException(
        UUID.fromString("c70a38f9-b770-4f2d-8c64-32cc583aac95"));
    Inbox message1 = inboxRepository.findByIdAndReceiver(
        UUID.fromString("a2bb9999-894d-4afc-9f7d-b104be716347"), account).orElseThrow();
    Inbox message2 = inboxRepository.findByIdAndReceiver(
        UUID.fromString("b2bb9999-894d-4afc-9f7d-b104be716347"), account).orElseThrow();
    Inbox message3 = inboxRepository.findByIdAndReceiver(
        UUID.fromString("c2bb9999-894d-4afc-9f7d-b104be716347"), account).orElseThrow();

    List<InboxGetAllResponseDTO> expectedResponseBody =
        Stream.of(message1, message2, message3)
            .map(msg -> new InboxGetAllResponseDTO(msg.getId(), msg.getMessage(), msg.getIsRead(),
                msg.getSentAt(), msg.getProduct().getId()))
            .toList();

    ObjectMapper objectMapper = JsonMapper.builder()
        .addModule(new JavaTimeModule())
        .build();

    ResultActions getMessage = mockMvc.perform(MockMvcRequestBuilders.get("/v1/inbox")
        .principal(() -> "usernameInbox"));
    getMessage
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
  }

  //##################
  //GET v1/inbox/{id}#
  //##################
  @Test
  @WithMockUser(username = "usernameInbox", roles = "USER")
  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, statements = {
      "INSERT INTO account (id, username, first_name, last_name, date_of_birth, email, password) VALUES ('c70a38f9-b770-4f2d-8c64-32cc583aac95', 'usernameInbox', 'firstnameInbox', 'lastnameInbox', '1990-01-01', 'inbox@example.com', '$2a$10$YltQfNKzHoF4Db1oUHtP/eODkthW90lPaouBw6Q1k/7keLcctilpm')",
      "INSERT INTO product_category VALUES ('d5509745-450f-4760-8bdd-ddc88d376b37', 'electronics')",
      "INSERT INTO product(id, name, product_category, price, condition, status, description, seller, buyer, color, production_year, created_at) VALUES ('e8fc64de-5a16-4b76-bac0-cf4a20052589', 'test', 'd5509745-450f-4760-8bdd-ddc88d376b37', 200, 0, 0, 'wow', 'c70a38f9-b770-4f2d-8c64-32cc583aac95', null, 0, 2019, now())",
      "INSERT INTO inbox (id, receiver_id, message, is_read, sent_at, product_id) VALUES ('d24b4a00-22f1-4ef2-a081-2c9b95f76156','c70a38f9-b770-4f2d-8c64-32cc583aac95', 'Test message', false, now(), 'e8fc64de-5a16-4b76-bac0-cf4a20052589')"
  })
  @Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = {
      "DELETE FROM inbox WHERE id = 'd24b4a00-22f1-4ef2-a081-2c9b95f76156'",
      "DELETE FROM account WHERE id = 'c70a38f9-b770-4f2d-8c64-32cc583aac95'",
      "DELETE FROM product",
      "DELETE FROM product_category"
  })
  public void getMessageById() throws Exception {
    Account account = userService.getAccountOrException(
        UUID.fromString("c70a38f9-b770-4f2d-8c64-32cc583aac95"));
    Inbox inbox = inboxRepository.findByIdAndReceiver(
        UUID.fromString("d24b4a00-22f1-4ef2-a081-2c9b95f76156"), account).orElseThrow();

    InboxGetAllResponseDTO expectedResponseBody = new InboxGetAllResponseDTO(
        inbox.getId(),
        inbox.getMessage(),
        inbox.getIsRead(),
        inbox.getSentAt(),
        inbox.getProduct().getId()
    );

    ObjectMapper objectMapper = JsonMapper.builder()
        .addModule(new JavaTimeModule())
        .build();

    ResultActions getMessage = mockMvc.perform(
        MockMvcRequestBuilders.get("/v1/inbox/e8fc64de-5a16-4b76-bac0-cf4a20052589")
            .principal(() -> "usernameInbox"));
    getMessage
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
  }

  //#####################
  //DELETE v1/inbox/{id}#
  //#####################
  @Test
  @WithMockUser(username = "usernameInbox", roles = "USER")
  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, statements = {
      "INSERT INTO account (id, username, first_name, last_name, date_of_birth, email, password) VALUES ('c70a38f9-b770-4f2d-8c64-32cc583aac95', 'usernameInbox', 'firstnameInbox', 'lastnameInbox', '1990-01-01', 'inbox@example.com', '$2a$10$YltQfNKzHoF4Db1oUHtP/eODkthW90lPaouBw6Q1k/7keLcctilpm')",
      "INSERT INTO product_category VALUES ('d5509745-450f-4760-8bdd-ddc88d376b37', 'electronics')",
      "INSERT INTO product(id, name, product_category, price, condition, status, description, seller, buyer, color, production_year, created_at) VALUES ('e8fc64de-5a16-4b76-bac0-cf4a20052589', 'test', 'd5509745-450f-4760-8bdd-ddc88d376b37', 200, 0, 0, 'wow', 'c70a38f9-b770-4f2d-8c64-32cc583aac95', null, 0, 2019, now())",
      "INSERT INTO inbox (id, receiver_id, message, is_read, sent_at, product_id) VALUES ('d24b4a00-22f1-4ef2-a081-2c9b95f76156','c70a38f9-b770-4f2d-8c64-32cc583aac95', 'Test message', false, now(), 'e8fc64de-5a16-4b76-bac0-cf4a20052589')"
  })
  @Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = {
      "DELETE FROM inbox WHERE id = 'd24b4a00-22f1-4ef2-a081-2c9b95f76156'",
      "DELETE FROM account WHERE id = 'c70a38f9-b770-4f2d-8c64-32cc583aac95'",
      "DELETE FROM product",
      "DELETE FROM product_category"
  })
  public void deleteMessageById() throws Exception {
    Account account = userService.getAccountOrException(
        UUID.fromString("c70a38f9-b770-4f2d-8c64-32cc583aac95"));

    ResultActions deleteMessage = mockMvc.perform(
        MockMvcRequestBuilders.delete("/v1/inbox/d24b4a00-22f1-4ef2-a081-2c9b95f76156")
            .principal(() -> "usernameInbox"));

    deleteMessage.andExpect(status().isOk());

    Assertions.assertTrue(
        inboxRepository.findByIdAndReceiver(UUID.fromString("d24b4a00-22f1-4ef2-a081-2c9b95f76156"),
            account).isEmpty());
  }
}