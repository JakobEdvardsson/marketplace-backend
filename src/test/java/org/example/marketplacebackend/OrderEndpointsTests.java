package org.example.marketplacebackend;

import org.example.marketplacebackend.DTO.incoming.OrderDTO;
import org.example.marketplacebackend.DTO.incoming.OrderItemDTO;
import org.example.marketplacebackend.service.ProductOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class OrderEndpointsTests {

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
  ProductOrderService productOrderService;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
      statements = """
          INSERT INTO account (id, first_name, last_name, date_of_birth, email, password, username) VALUES ('3a45dc5e-2a30-41ba-b488-ca4b113ea5ee', 'Ken', 'Thompson', '1943-02-04', 'ken@example.com', '$2a$10$gIwb60Eio1J1UYWqCrV4je9kAzsqra0kzwg5fcKRCauzGUQ2xmx3q', 'ken');
          INSERT INTO product_category VALUES ('d5509745-450f-4760-8bdd-ddc88d376b37', 'electronics');
          INSERT INTO product (id, name, product_category, price, condition, status, description, seller, buyer, color, production_year) VALUES ('3ce17658-9107-4154-9ead-e22c5d6508a5', 'name' ,'d5509745-450f-4760-8bdd-ddc88d376b37', 500, 0, 0, 'description', '3a45dc5e-2a30-41ba-b488-ca4b113ea5ee', null, 0, 2024);
          """)
  @Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD,
      statements = """
          DELETE FROM order_item where product_id = '3ce17658-9107-4154-9ead-e22c5d6508a5';
          DELETE FROM product_order WHERE buyer_id = '3a45dc5e-2a30-41ba-b488-ca4b113ea5ee';
          DELETE FROM product WHERE id = '3ce17658-9107-4154-9ead-e22c5d6508a5';
          DELETE FROM product_category WHERE id = 'd5509745-450f-4760-8bdd-ddc88d376b37';
          DELETE FROM account WHERE id = '3a45dc5e-2a30-41ba-b488-ca4b113ea5ee';
          """)
  public void createOrderSuccess() throws Exception {
    List<OrderItemDTO> orderItemDTOS = new ArrayList<>();
    OrderItemDTO orderItemDTO = new OrderItemDTO(
        UUID.fromString("3ce17658-9107-4154-9ead-e22c5d6508a5"));
    orderItemDTOS.add(orderItemDTO);

    OrderDTO orderDTO = new OrderDTO(orderItemDTOS);
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(orderDTO);

    ResultActions createOrder = mockMvc.perform(post("/v1/orders")
        .principal(() -> "ken")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json)
    );

    createOrder
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
      statements = """
          INSERT INTO account (id, first_name, last_name, date_of_birth, email, password, username) VALUES ('3a45dc5e-2a30-41ba-b488-ca4b113ea5ee', 'Ken', 'Thompson', '1943-02-04', 'ken@example.com', '$2a$10$gIwb60Eio1J1UYWqCrV4je9kAzsqra0kzwg5fcKRCauzGUQ2xmx3q', 'ken');
          INSERT INTO product_category VALUES ('d5509745-450f-4760-8bdd-ddc88d376b37', 'electronics');
          INSERT INTO product (id, name, product_category, price, condition, status, description, seller, buyer, color, production_year) VALUES ('3ce17658-9107-4154-9ead-e22c5d6508a5', 'name' ,'d5509745-450f-4760-8bdd-ddc88d376b37', 500, 0, 0, 'description', '3a45dc5e-2a30-41ba-b488-ca4b113ea5ee', null, 0, 2024);
          """)
  @Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD,
      statements = """
          DELETE FROM product WHERE id = '3ce17658-9107-4154-9ead-e22c5d6508a5';
          DELETE FROM product_category WHERE id = 'd5509745-450f-4760-8bdd-ddc88d376b37';
          DELETE FROM account WHERE id = '3a45dc5e-2a30-41ba-b488-ca4b113ea5ee';
          """)
  public void createOrderFail() throws Exception {
    ResultActions createOrder = mockMvc.perform(post("/v1/orders")
        .principal(() -> "ken")
        .contentType(MediaType.APPLICATION_JSON)
        .content("")
    );

    createOrder
        .andExpect(status().isBadRequest());
  }

  @Test
  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
      statements = """
          INSERT INTO account (id, first_name, last_name, date_of_birth, email, password, username) VALUES ('3a45dc5e-2a30-41ba-b488-ca4b113ea5ee', 'Ken', 'Thompson', '1943-02-04', 'ken@example.com', '$2a$10$gIwb60Eio1J1UYWqCrV4je9kAzsqra0kzwg5fcKRCauzGUQ2xmx3q', 'ken');
          INSERT INTO product_category VALUES ('d5509745-450f-4760-8bdd-ddc88d376b37', 'electronics');
          INSERT INTO product (id, name, product_category, price, condition, status, description, seller, buyer, color, production_year) VALUES ('3ce17658-9107-4154-9ead-e22c5d6508a5', 'name' ,'d5509745-450f-4760-8bdd-ddc88d376b37', 500, 0, 0, 'description', '3a45dc5e-2a30-41ba-b488-ca4b113ea5ee', null, 0, 2024);
          """)
  @Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD,
      statements = """
          DELETE FROM order_item where product_id = '3ce17658-9107-4154-9ead-e22c5d6508a5';
          DELETE FROM product_order WHERE buyer_id = '3a45dc5e-2a30-41ba-b488-ca4b113ea5ee';
          DELETE FROM product WHERE id = '3ce17658-9107-4154-9ead-e22c5d6508a5';
          DELETE FROM product_category WHERE id = 'd5509745-450f-4760-8bdd-ddc88d376b37';
          DELETE FROM account WHERE id = '3a45dc5e-2a30-41ba-b488-ca4b113ea5ee';
          """)
  public void getAllOrdersSuccess() throws Exception {
    List<OrderItemDTO> orderItemDTOS = new ArrayList<>();
    OrderItemDTO orderItemDTO = new OrderItemDTO(
        UUID.fromString("3ce17658-9107-4154-9ead-e22c5d6508a5"));
    orderItemDTOS.add(orderItemDTO);

    OrderDTO orderDTO = new OrderDTO(orderItemDTOS);
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(orderDTO);

    mockMvc.perform(post("/v1/orders")
        .principal(() -> "ken")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json)
    );

    ResultActions response = mockMvc.perform(get("/v1/orders")
        .principal(() -> "ken")
        .contentType(MediaType.APPLICATION_JSON)
    );

    response
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

  }

  @Test
  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
      statements = """
          INSERT INTO account (id, first_name, last_name, date_of_birth, email, password, username) VALUES ('3a45dc5e-2a30-41ba-b488-ca4b113ea5ee', 'Ken', 'Thompson', '1943-02-04', 'ken@example.com', '$2a$10$gIwb60Eio1J1UYWqCrV4je9kAzsqra0kzwg5fcKRCauzGUQ2xmx3q', 'ken');
          INSERT INTO product_category VALUES ('d5509745-450f-4760-8bdd-ddc88d376b37', 'electronics');
          INSERT INTO product (id, name, product_category, price, condition, status, description, seller, buyer, color, production_year) VALUES ('3ce17658-9107-4154-9ead-e22c5d6508a5', 'name' ,'d5509745-450f-4760-8bdd-ddc88d376b37', 500, 0, 0, 'description', '3a45dc5e-2a30-41ba-b488-ca4b113ea5ee', null, 0, 2024);
          """)
  @Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD,
      statements = """
          DELETE FROM order_item where product_id = '3ce17658-9107-4154-9ead-e22c5d6508a5';
          DELETE FROM product_order WHERE buyer_id = '3a45dc5e-2a30-41ba-b488-ca4b113ea5ee';
          DELETE FROM product WHERE id = '3ce17658-9107-4154-9ead-e22c5d6508a5';
          DELETE FROM product_category WHERE id = 'd5509745-450f-4760-8bdd-ddc88d376b37';
          DELETE FROM account WHERE id = '3a45dc5e-2a30-41ba-b488-ca4b113ea5ee';
          """)
  public void getOrderSuccess() throws Exception {
    List<OrderItemDTO> orderItemDTOS = new ArrayList<>();
    OrderItemDTO orderItemDTO = new OrderItemDTO(
        UUID.fromString("3ce17658-9107-4154-9ead-e22c5d6508a5"));
    orderItemDTOS.add(orderItemDTO);

    OrderDTO orderDTO = new OrderDTO(orderItemDTOS);
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(orderDTO);

    ResultActions responseCreatedOrder = mockMvc.perform(post("/v1/orders")
        .principal(() -> "ken")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json)
    );

    String responseString = responseCreatedOrder.andReturn().getResponse().getContentAsString();
    JsonNode root = mapper.readTree(responseString);
    JsonNode orderId = root.get("orderId");
    String endPoint = "/v1/orders/" + orderId.asText();

    ResultActions response = mockMvc.perform(get(
        endPoint)
        .principal(() -> "ken")
        .contentType(MediaType.APPLICATION_JSON)
    );

    response
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
      statements = """
          INSERT INTO account (id, first_name, last_name, date_of_birth, email, password, username) VALUES ('3a45dc5e-2a30-41ba-b488-ca4b113ea5ee', 'Ken', 'Thompson', '1943-02-04', 'ken@example.com', '$2a$10$gIwb60Eio1J1UYWqCrV4je9kAzsqra0kzwg5fcKRCauzGUQ2xmx3q', 'ken');
          INSERT INTO product_category VALUES ('d5509745-450f-4760-8bdd-ddc88d376b37', 'electronics');
          INSERT INTO product (id, name, product_category, price, condition, status, description, seller, buyer, color, production_year) VALUES ('3ce17658-9107-4154-9ead-e22c5d6508a5', 'name' ,'d5509745-450f-4760-8bdd-ddc88d376b37', 500, 0, 0, 'description', '3a45dc5e-2a30-41ba-b488-ca4b113ea5ee', null, 0, 2024);
          """)
  @Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD,
      statements = """
          DELETE FROM order_item where product_id = '3ce17658-9107-4154-9ead-e22c5d6508a5';
          DELETE FROM product_order WHERE buyer_id = '3a45dc5e-2a30-41ba-b488-ca4b113ea5ee';
          DELETE FROM product WHERE id = '3ce17658-9107-4154-9ead-e22c5d6508a5';
          DELETE FROM product_category WHERE id = 'd5509745-450f-4760-8bdd-ddc88d376b37';
          DELETE FROM account WHERE id = '3a45dc5e-2a30-41ba-b488-ca4b113ea5ee';
          """)
  public void getOrderFail() throws Exception {
    String endPoint = "/v1/orders/" + UUID.randomUUID();
    ResultActions response = mockMvc.perform(get(
        endPoint)
        .principal(() -> "ken")
        .contentType(MediaType.APPLICATION_JSON)
    );

    response.andExpect(status().isNotFound());
  }

  // TODO: Create this test
  @Test
  public void testMultipleOrdersSameProduct() {

  }
}
