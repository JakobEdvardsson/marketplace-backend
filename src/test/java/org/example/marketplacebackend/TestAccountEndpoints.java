package org.example.marketplacebackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.marketplacebackend.DTO.incoming.UserDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestAccountEndpoints {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AccountRepository accountRepository;

  @Test
  @Transactional
  void testRegisterUser() throws Exception {
    UserDTO account = new UserDTO("test", "test@mail.com",
        "test@mail.com", "user", "test123", Date.valueOf("1993-03-11"));

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(account);

    ResultActions createUser = mockMvc.perform(post("/v1/accounts/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json));

    createUser.andExpect(status().isCreated());
  }

  @Test
  @WithMockUser
  void deleteLoggedInUser() throws Exception {
    Account account = new Account();
    account.setFirst_name("test");
    account.setLast_name("testsson");
    account.setUsername("user");
    account.setPassword("test123");
    account.setEmail("test@mail.com");
    account.setDate_of_birth(Date.valueOf("1993-03-11"));

    accountRepository.save(account);

    ResultActions createUser = mockMvc.perform(delete("/v1/accounts"));

    createUser.andExpect(status().isOk());
  }

  @AfterEach
  void tearDown() {
    accountRepository.deleteByUsername("user");
  }
}
