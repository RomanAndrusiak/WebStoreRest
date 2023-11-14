package roman.andrusiak.test.task.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import roman.andrusiak.test.task.dto.CartItemDto;
import roman.andrusiak.test.task.dto.OrderDto;
import roman.andrusiak.test.task.exceptions.AccessDeniedException;
import roman.andrusiak.test.task.exceptions.CartItemNotFoundException;
import roman.andrusiak.test.task.models.*;
import roman.andrusiak.test.task.security.PersonDetails;
import roman.andrusiak.test.task.services.CarItemService;
import roman.andrusiak.test.task.services.OrderService;
import roman.andrusiak.test.task.services.PersonDetailsService;
import roman.andrusiak.test.task.services.ProductService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final PersonDetailsService personDetailsService;
    private final ProductService productService;
    private final CarItemService carItemService;

    @Autowired
    public OrderController(OrderService orderService, PersonDetailsService personDetailsService, ProductService productService, CarItemService carItemService) {
        this.orderService = orderService;
        this.personDetailsService = personDetailsService;
        this.productService = productService;
        this.carItemService = carItemService;

    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@AuthenticationPrincipal PersonDetails personDetails,
                                                @RequestBody List<CartItemDto> cartItemDtos,
                                                @RequestParam("shippingAddress") String shippingAddress) {
        Order newOrder = new Order();
        newOrder.setPerson(personDetails.getPerson());
        newOrder.setStatus(OrderStatus.PENDING_PAYMENT);
        newOrder.setCreatedAt(LocalDateTime.now());
        newOrder.setShippingAddress(shippingAddress);
        for (CartItemDto cartItemDto : cartItemDtos) {
            int productId = cartItemDto.getProductId();
            Optional<Product> productOpt = productService.getOneById(productId);
            if (productOpt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            int countLeft = productOpt.get().getCountLeft();
            int count = cartItemDto.getQuantity();
            if (countLeft < count) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Product product = productOpt.get();
            product.setCountLeft(countLeft - count);
            productService.update(product, productId);
            try {
                carItemService.deleteCartItem(cartItemDto.getId(), personDetails.getPerson());
            } catch (CartItemNotFoundException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (AccessDeniedException e) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        List<CartItem> cartItems = cartItemDtos.stream()
                .map(dto -> {
                    Product product = productService.getOneById(dto.getProductId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found"));
                    CartItem cartItem = new CartItem();
                    cartItem.setProduct(product);
                    cartItem.setQuantity(dto.getQuantity());
                    cartItem.setPerson(personDetails.getPerson());
                    return cartItem;
                })
                .collect(Collectors.toList());
        newOrder.setCartItems(cartItems);
        Order savedOrder = orderService.createOrder(newOrder);
        OrderDto orderDto = convertOrderToOrderDto(savedOrder);
        return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
    }


    @GetMapping("/all")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderDto> orderDtos = orders.stream()
                .map(this::convertOrderToOrderDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<OrderDto>> getAllOrdersByUser(@AuthenticationPrincipal PersonDetails personDetails) {
        List<Order> orders = orderService.getOrdersByPersonId(personDetails.getPerson().getId());
        List<OrderDto> orderDtos = orders.stream()
                .map(this::convertOrderToOrderDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    @GetMapping("/all/status")
    public ResponseEntity<List<OrderDto>> getOrdersByStatus(@RequestParam("status") OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        List<OrderDto> orderDtos = orders.stream()
                .map(this::convertOrderToOrderDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    private OrderDto convertOrderToOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setPerson_id(order.getPerson().getId());
        orderDto.setCartItems(order.getCartItems().stream()
                .map(this::convertCartItemToCartItemDto)
                .collect(Collectors.toList()));
        orderDto.setStatus(order.getStatus().toString());
        orderDto.setCreatedAt(order.getCreatedAt());
        orderDto.setShippingAddress(order.getShippingAddress());
        return orderDto;
    }

    private CartItemDto convertCartItemToCartItemDto(CartItem cartItem) {
        return new CartItemDto(
                cartItem.getId(),
                cartItem.getPerson().getUsername(),
                cartItem.getProduct().getId(),
                cartItem.getQuantity()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable int id, @AuthenticationPrincipal PersonDetails personDetails) {
        Optional<Order> optionalOrder = orderService.getOrderById(id);

        if (optionalOrder.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Order order = optionalOrder.get();

        if (order.getPerson().getId() != personDetails.getPerson().getId() && !"ROLE_ADMIN".equals(personDetails.getPerson().getRole())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        OrderDto orderDto = convertOrderToOrderDto(order);
        return new ResponseEntity<>(orderDto, HttpStatus.OK);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable int id, @RequestParam("status") OrderStatus status) {
        Optional<Order> order = orderService.updateOrderStatus(id, status);
        if (order.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(convertOrderToOrderDto(order.get()), HttpStatus.OK);
    }
}