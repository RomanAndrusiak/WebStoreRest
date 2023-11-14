package roman.andrusiak.test.task.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import roman.andrusiak.test.task.dto.CartItemDto;
import roman.andrusiak.test.task.dto.CartItemDto2;
import roman.andrusiak.test.task.exceptions.AccessDeniedException;
import roman.andrusiak.test.task.exceptions.CartItemNotFoundException;
import roman.andrusiak.test.task.models.CartItem;
import roman.andrusiak.test.task.security.PersonDetails;
import roman.andrusiak.test.task.services.CarItemService;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/items")
public class CartItemController {
    private final CarItemService cartItemService;


    @Autowired
    public CartItemController(CarItemService cartItemService) {
        this.cartItemService = cartItemService;

    }

    @GetMapping()
    public ResponseEntity<List<CartItemDto2>> getCartItems(@AuthenticationPrincipal PersonDetails personDetails) {
        List<CartItemDto2> cartItems = cartItemService.getAllNotTaken(personDetails.getUsername());
        return ResponseEntity.ok(cartItems);
    }

    private CartItemDto convertToDto(CartItem cartItem) {
        return new CartItemDto(cartItem.getId(), cartItem.getPerson().getUsername(),
                cartItem.getProduct().getId(), cartItem.getQuantity());
    }

    @PostMapping("/add")
    public ResponseEntity<CartItemDto> addCartItem(@RequestBody CartItemDto cartItemDto, @AuthenticationPrincipal PersonDetails personDetails) {
        CartItem cartItem = cartItemService.addCartItem(personDetails.getPerson(), cartItemDto.getProductId(), cartItemDto.getQuantity());
        CartItemDto responseDto = convertToDto(cartItem);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<CartItemDto> updateCartItemQuantity(@PathVariable("id") int cartItemId, @RequestParam int quantity) {
        CartItem cartItem = null;
        try {
            cartItem = cartItemService.updateCartItemQuantity(cartItemId, quantity);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        CartItemDto responseDto = convertToDto(cartItem);
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable("id") int id, @AuthenticationPrincipal PersonDetails personDetails) {
        try {
            cartItemService.deleteCartItem(id, personDetails.getPerson());
        } catch (CartItemNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


        return ResponseEntity.ok().build();
    }

}
