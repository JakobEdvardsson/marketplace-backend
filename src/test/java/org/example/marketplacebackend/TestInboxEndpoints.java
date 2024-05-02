package org.example.marketplacebackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.marketplacebackend.DTO.outgoing.InboxGetAllResponseDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.Inbox;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Date;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class TestInboxEndpoints {
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

  //v1/inbox/{id}



  @Test
  @WithMockUser(username="user")
  @Sql(statements = {
      "INSERT INTO account (id, username, first_name, last_name, date_of_birth, email, password) VALUES ('c70a38f9-b770-4f2d-8c64-32cc583aac95', 'usernameInbox', 'fistnameInbox', 'lastnameInbox', '1990-01-01', 'inbox@example.com', 'passwordInbox')",
      "INSERT INTO inbox (receiver_id, message, is_read, id, sent_at) VALUES ('c70a38f9-b770-4f2d-8c64-32cc583aac95', 'Test message', false, 'd24b4a00-22f1-4ef2-a081-2c9b95f76156', now())"
  })
  public void getMessageById() throws Exception {
    Account account = new Account();
    account.setId(UUID.fromString("c70a38f9-b770-4f2d-8c64-32cc583aac95"));
    account.setUsername("usernameInbox");
    account.setFirst_name("firstnameInbox");
    account.setLast_name("lastnameInbox");
    account.setDate_of_birth(Date.valueOf("1990-01-01"));
    account.setEmail("inbox@example.com");
    account.setPassword("passwordInbox");

    /*Inbox inbox = new Inbox();
    inbox.setReceiver(account);
    inbox.setMessage("Test message");
    inbox.setRead(false);
    inbox.setId(UUID.fromString("d24b4a00-22f1-4ef2-a081-2c9b95f76156"));

    ObjectMapper objectMapper = new ObjectMapper();

    String expectedJson = objectMapper.writeValueAsString(new InboxGetAllResponseDTO(
        UUID.fromString("d24b4a00-22f1-4ef2-a081-2c9b95f76156"),
        "Test message",
        false,

    ))
    mockMvc.perform(get("/v1/inbox/d24b4a00-22f1-4ef2-a081-2c9b95f76156"));
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(inbox)));
  }

   */

}
}
