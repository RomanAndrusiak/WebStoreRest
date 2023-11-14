package roman.andrusiak.test.task.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import roman.andrusiak.test.task.models.Product;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductListDto {
    private List<Product> products;
    private boolean isAdmin;
}
