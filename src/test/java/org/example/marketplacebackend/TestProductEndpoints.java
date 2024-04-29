package org.example.marketplacebackend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.marketplacebackend.DTO.incoming.ProductDTO;
import org.example.marketplacebackend.service.ProductImageService;
import org.example.marketplacebackend.service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestProductEndpoints {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private ProductService productService;

  @Autowired
  private ProductImageService productImageService;

  private ArrayList<String> imageUrlsStrings;

  @BeforeEach
  public void setup() {
    // init MockMvc Object and build
    imageUrlsStrings = new ArrayList<>();
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  @WithMockUser
  void uploadProductSuccess() throws Exception {
    UUID productType = UUID.fromString("d5509745-450f-4760-8bdd-ddc88d376b37");
    ProductDTO product = new ProductDTO("test", productType,
        500, 0, "wow amazing",
        null, null);

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(product);
    Resource file = new ClassPathResource("test_data/test.jpg");
    Resource file2 = new ClassPathResource("test_data/test2.jpg");

    MockMultipartFile jsonProduct = new MockMultipartFile("json", "", "application/json",
        json.getBytes());
    MockMultipartFile imageFile = new MockMultipartFile("data", file.getFilename(),
        "multipart/form-data", file.getContentAsByteArray());
    MockMultipartFile imageFile2 = new MockMultipartFile("data", file2.getFilename(),
        "multipart/form-data", file2.getContentAsByteArray());

    ResultActions createProduct = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/products")
        .file(jsonProduct)
        .file(imageFile)
        .file(imageFile2));

    String response = createProduct
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode jsonNode = objectMapper.readTree(response);
    JsonNode imageUrls = jsonNode.get("imageUrls");

    for (int i = 0; i < imageUrls.size(); i++) {
      imageUrlsStrings.add(i, imageUrls.get(i).toString());
    }
  }

  @Test
  @WithMockUser
  void getAllProducts() throws Exception {
    ResultActions getProducts = mockMvc.perform(get("/v1/products"));

    getProducts
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @WithMockUser
  void getAllProductsByCategorySuccess() throws Exception {
    ResultActions getProducts = mockMvc.perform(get("/v1/products?category=kebab"));

    String response = getProducts
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse().getContentAsString();
    System.out.println(response);
  }

  @Test
  @WithMockUser
  void getAllProductsByCategoryFail() throws Exception {
    ResultActions getProducts = mockMvc.perform(get("/v1/products?category=asdasdasdasdasd"));

    getProducts.andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser
  void deleteProductSuccess() throws Exception {
    String responseCreateProduct = Utils.createProduct(mockMvc);

    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(responseCreateProduct);
    JsonNode imageUrls = jsonNode.get("imageUrls");
    for (int i = 0; i < imageUrls.size(); i++) {
      imageUrlsStrings.add(i, imageUrls.get(i).toString());
    }

    String id = jsonNode.get("id").toString();
    String endPoint = "/v1/products/" + id.substring(1, id.length() - 1);

    ResultActions getProducts = mockMvc.perform(delete(endPoint));
    getProducts.andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  public void getProductByIdSuccessful() throws Exception {
    String id = "798bdcaf-03c7-4fec-8b87-482ed7cac83d";
    String endPoint = "/v1/products/" + id;

    ResultActions getProducts = mockMvc.perform(get(endPoint));
    getProducts
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @WithMockUser
  public void getProductByIdFail() throws Exception {
    String id = "798bdcaf";
    String endPoint = "/v1/products/" + id;

    ResultActions getProducts = mockMvc.perform(get(endPoint));
    getProducts.andExpect(status().isBadRequest());
  }

  @AfterEach
  void tearDown() {
    // NOTE: Remove all code in here to check the actual values in the DB
    // and the images in src/main/resources/images/
    for (String url : imageUrlsStrings) {
      String cleanUrl = url.substring(1, url.length() - 1);
      File file = new File("src/main/resources/images/" + cleanUrl);
      if (file.delete()) {
        System.out.println("Deleted the file: " + file.getName());
        productImageService.deleteProductImageByImageUrl(cleanUrl);
      } else {
        System.out.println("Failed to delete the file: " + file.getName());
      }
    }

    productService.deleteByName("test");
  }

}
