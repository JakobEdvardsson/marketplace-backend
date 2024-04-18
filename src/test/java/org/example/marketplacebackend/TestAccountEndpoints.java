package org.example.marketplacebackend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.sql.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestAccountEndpoints {
  @Autowired
  private MockMvc mockMvc;

  @Test
  void testRegisterUser() throws Exception {
    Account account = new Account();
    account.setUsername("test");
    account.setEmail("test@mail.com");
    account.setPassword("test123");
    account.setFirst_name("test1");
    account.setLast_name("testsson");
    account.setDate_of_birth(Date.valueOf("1993-03-11"));

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(account);

    ResultActions createUser = mockMvc.perform(post("/v1/accounts/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json));

    createUser.andExpect(status().isCreated());

    // NOTE: Does not work yet because of security configurations
    // String response = createUser.andReturn().getResponse().getContentAsString();
    // JsonNode rootNode = objectMapper.readTree(response);
    // String id = rootNode.get("id").toString();
    // ResultActions deleteUser = mockMvc.perform(delete("/v1/accounts/" + id)
    //     .contentType(MediaType.APPLICATION_JSON));

    // deleteUser.andExpect(status().isOk());
  }
}
