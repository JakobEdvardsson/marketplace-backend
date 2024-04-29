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
  public static String createUser(MockMvc mockMvc) throws Exception {
    UUID productType = UUID.fromString("d5509745-450f-4760-8bdd-ddc88d376b37");
    UUID seller = UUID.fromString("dc254b85-6610-43c9-9f48-77a80b798158");
    ProductDTO product = new ProductDTO("test", productType,
        500, 0, "wow amazing", seller,
        null, null);

    Resource file = new ClassPathResource("test_data/test.jpg");
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(product);

    MockMultipartFile jsonProduct = new MockMultipartFile("json", "", "application/json",
        json.getBytes());
    MockMultipartFile imageFile = new MockMultipartFile("data", file.getFilename(),
        "multipart/form-data", file.getContentAsByteArray());

    ResultActions createUser = mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/products")
        .file(jsonProduct)
        .file(imageFile));

    String responseCreateUser = createUser.andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    return responseCreateUser;
  }

}
