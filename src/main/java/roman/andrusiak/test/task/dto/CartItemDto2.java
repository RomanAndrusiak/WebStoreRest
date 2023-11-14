package roman.andrusiak.test.task.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto2 {
    private int id;
    private String username;
    private int productId;
    private int quantity;
    private Integer orderId;
}