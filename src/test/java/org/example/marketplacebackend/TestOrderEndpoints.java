package org.example.marketplacebackend;

import org.example.marketplacebackend.DTO.incoming.OrderDTO;
import org.example.marketplacebackend.model.Account;
import org.example.marketplacebackend.model.OrderItem;
import org.example.marketplacebackend.model.ProductOrder;
import org.example.marketplacebackend.service.ProductOrderService;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class TestOrderEndpoints {
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
  private WebApplicationContext webApplicationContext;

  @Autowired
  private ProductOrderService productOrderService;

  @BeforeEach
  public void setup() {
    // init MockMvc Object and build
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
      statements = """
      INSERT INTO account (id, first_name, last_name, date_of_birth, email, password, username) VALUES ('3a45dc5e-2a30-41ba-b488-ca4b113ea5ee', 'Ken', 'Thompson', '1943-02-04', 'ken@example.com', '$2a$10$gIwb60Eio1J1UYWqCrV4je9kAzsqra0kzwg5fcKRCauzGUQ2xmx3q', 'ken');
      INSERT INTO product_category VALUES ('d5509745-450f-4760-8bdd-ddc88d376b37', 'electronics');
      """)
  @Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD,
      statements = """
      DELETE FROM account WHERE id = '3a45dc5e-2a30-41ba-b488-ca4b113ea5ee';
      DELETE FROM product_category WHERE id = 'd5509745-450f-4760-8bdd-ddc88d376b37';
      """)
  public void createOrderSuccess() throws Exception {
    Account buyer = new Account();
    ProductOrder order = new ProductOrder();
    order.setBuyer(buyer);

    List<OrderItem> orderItems = new ArrayList<>();
    OrderItem orderItem1 = new OrderItem();
    orderItem1.setOrder(order);

    OrderDTO orderDTO = new OrderDTO(orderItems);
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(orderDTO);

    ResultActions createOrder = mockMvc.perform(post("/v1/orders")
        .principal(() -> "ken")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json)
    );

    String response = createOrder
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    System.out.println(response);
  }
}
