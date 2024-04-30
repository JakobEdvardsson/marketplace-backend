package org.example.marketplacebackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.marketplacebackend.DTO.incoming.ProductDTO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class Utils {

  public static String createProduct(MockMvc mockMvc, String productCategoryId, String principalName) throws Exception {
    UUID productCategory = UUID.fromString(productCategoryId);
    ProductDTO product = new ProductDTO("test", productCategory,
        500, 0, "wow amazing",
        null, null);

    Resource file = new ClassPathResource("test_data/test.jpg");
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(product);

    MockMultipartFile jsonProduct = new MockMultipartFile("json", "", "application/json",
        json.getBytes());
    MockMultipartFile imageFile = new MockMultipartFile("data", file.getFilename(),
        "multipart/form-data", file.getContentAsByteArray());

    ResultActions createProduct = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/products")
        .file(jsonProduct)
        .file(imageFile)
        .principal(() -> principalName)
    );

      return createProduct.andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();
  }

}
