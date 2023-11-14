package roman.andrusiak.test.task.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import roman.andrusiak.test.task.models.Order;
import roman.andrusiak.test.task.models.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findOrdersByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime createdAtBefore);
    List<Order> getOrdersByPersonId(int id);
    List<Order> findAllByStatus(OrderStatus status);
}