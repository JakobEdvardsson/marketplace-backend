package org.example.marketplacebackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.marketplacebackend.DTO.incoming.ProductDTO;
import org.junit.jupiter.api.AfterEach;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestProductEndpoints {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @WithMockUser
  void uploadProduct() throws Exception {
    String[] images = new String[1];
    images[0] = "test_data/test.jpg";
    UUID productType = UUID.fromString("d5509745-450f-4760-8bdd-ddc88d376b37");
    UUID seller = UUID.fromString("dc254b85-6610-43c9-9f48-77a80b798158");
    ProductDTO product = new ProductDTO("test", productType,
        500, 0, "wow amazing", seller,
        images, 0, 0);

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(product);
    Resource file = new ClassPathResource("test_data/test.jpg");
    System.out.println(file.getContentAsByteArray().length);

    MockMultipartFile jsonProduct = new MockMultipartFile("json", "", "application/json",
        json.getBytes());
    MockMultipartFile imageFile = new MockMultipartFile("data", file.getFilename(),
        String.valueOf(MediaType.MULTIPART_FORM_DATA), file.getContentAsByteArray());


    ResultActions createUser = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/products")
            .file(jsonProduct)
            .file(imageFile));

        createUser.andExpect(status().isCreated());
  }

  @AfterEach
  void tearDown() {
  }
}
