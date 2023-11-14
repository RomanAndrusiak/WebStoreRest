package roman.andrusiak.test.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import roman.andrusiak.test.task.models.Product;
import roman.andrusiak.test.task.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ProductTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateProductSuccessfully() throws Exception {
        Product productToCreate = new Product();
        productToCreate.setName("Iphone 15 pro");
        productToCreate.setDescription("Iphone 15 pro 128gb");
        productToCreate.setCountLeft(33);
        productToCreate.setPrice(1222.99);

        ObjectMapper objectMapper = new ObjectMapper();
        String productJson = objectMapper.writeValueAsString(productToCreate);

        Mockito.when(productService.save(any(Product.class))).thenReturn(productToCreate);

        mockMvc.perform(post("/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson)
                        .with(csrf()))
                .andExpect(status().isCreated());

        Mockito.verify(productService, Mockito.times(1)).save(any(Product.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateProductValidationError() throws Exception {
        Product productToCreate = new Product();
        productToCreate.setName("C");
        productToCreate.setDescription("");
        productToCreate.setCountLeft(-10);
        productToCreate.setPrice(-1.00);

        ObjectMapper objectMapper = new ObjectMapper();
        String productJson = objectMapper.writeValueAsString(productToCreate);

        mockMvc.perform(post("/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].defaultMessage", hasSize(4)));

        Mockito.verify(productService, Mockito.never()).save(any(Product.class));
    }
}