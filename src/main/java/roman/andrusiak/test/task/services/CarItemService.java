package roman.andrusiak.test.task.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roman.andrusiak.test.task.dto.CartItemDto2;
import roman.andrusiak.test.task.exceptions.AccessDeniedException;
import roman.andrusiak.test.task.exceptions.CartItemNotFoundException;
import roman.andrusiak.test.task.models.CartItem;
import roman.andrusiak.test.task.models.Person;
import roman.andrusiak.test.task.models.Product;
import roman.andrusiak.test.task.repositories.CartItemRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CarItemService {
    private final CartItemRepository cartItemRepository;
    private final PersonDetailsService personDetailsService;
    private final ProductService productService;
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public CarItemService(CartItemRepository cartItemRepository, PersonDetailsService personDetailsService,
                          ProductService productService, JdbcTemplate jdbcTemplate) {
        this.cartItemRepository = cartItemRepository;
        this.personDetailsService = personDetailsService;
        this.productService = productService;

        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CartItem> getAllBy(String username) {
        return cartItemRepository.findByPersonUsername(username);
    }

    @Transactional
    public void deleteCartItem(int cartItemId, Person person) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);

        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            if (cartItem.getPerson().getUsername().equals(person.getUsername())) {
                cartItemRepository.delete(cartItem);
                return;
            } else {
                throw new AccessDeniedException("Problem with access");
            }
        }

        throw new CartItemNotFoundException("Not found");
    }

    @Transactional
    public void deleteItem(int cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).stream().findAny().orElse(null);
        cartItemRepository.delete(cartItem);

    }

    public List<CartItemDto2> getAllNotTaken(String username) {
        return jdbcTemplate.query("Select* from cart_item where username = ? and order_id is null"
                , new Object[]{username},
                new BeanPropertyRowMapper<>(CartItemDto2.class));
    }

    public CartItem addCartItem(Person person, int productId, int quantity) {
        Product product = productService.getOneById(productId).orElse(null);
        if (product == null) {
            throw new NoSuchElementException();
        } else if (product.getCountLeft() - quantity < 0) {
            throw new IllegalArgumentException();
        }
        CartItem cartItem = new CartItem();
        cartItem.setPerson(person);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    public Optional<CartItem> findById(int id) {
        return cartItemRepository.findById(id);
    }

    public CartItem updateCartItemQuantity(int cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElse(null);
        if (cartItem == null) {
            throw new NoSuchElementException();
        } else if (cartItem.getProduct().getCountLeft() - quantity < 0) {
            throw new IllegalArgumentException();
        }
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }
}
