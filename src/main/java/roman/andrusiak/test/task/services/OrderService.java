package roman.andrusiak.test.task.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import roman.andrusiak.test.task.models.CartItem;
import roman.andrusiak.test.task.models.Order;
import roman.andrusiak.test.task.models.OrderStatus;
import roman.andrusiak.test.task.models.Product;
import roman.andrusiak.test.task.repositories.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final CarItemService carItemService;

    @Autowired
    public OrderService(OrderRepository orderRepository, ProductService productService, CarItemService carItemService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.carItemService = carItemService;
    }

    @Scheduled(fixedRate = 60000)
    public void cancelUnpaidOrders() {
        LocalDateTime cancellationThreshold = LocalDateTime.now().minusMinutes(10);
        List<Order> unpaidOrders = orderRepository.findOrdersByStatusAndCreatedAtBefore(OrderStatus.PENDING_PAYMENT, cancellationThreshold);

        unpaidOrders.forEach(order -> {
            List<CartItem> items = order.getCartItems();
            items.forEach(item -> {
                int productId = item.getProduct().getId();
                Product product = productService.getOneById(productId).get();
                int countLeft = product.getCountLeft();
                int count = item.getQuantity();
                product.setCountLeft(countLeft + count);
                productService.update(product, productId);
                carItemService.deleteItem(item.getId());
            });
            orderRepository.delete(order);
        });
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(int id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> updateOrderStatus(int id, OrderStatus status) {
        Optional<Order> order = orderRepository.findById(id);
        order.ifPresent(o -> {
            o.setStatus(status);
            orderRepository.save(o);
        });
        return order;
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findAllByStatus(status);
    }

    public List<Order> getOrdersByPersonId(int id) {
        return orderRepository.getOrdersByPersonId(id);
    }
}
