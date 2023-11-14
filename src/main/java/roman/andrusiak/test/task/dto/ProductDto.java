package roman.andrusiak.test.task.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import roman.andrusiak.test.task.models.Product;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Product product;
    private boolean isAdmin;
}
