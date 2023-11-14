package roman.andrusiak.test.task.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private int id;
    private int person_id;
    private List<CartItemDto> cartItems;
    private String status;
    private LocalDateTime createdAt;
    private String shippingAddress;
}
