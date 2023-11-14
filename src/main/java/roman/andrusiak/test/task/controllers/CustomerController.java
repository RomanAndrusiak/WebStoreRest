package roman.andrusiak.test.task.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import roman.andrusiak.test.task.dto.ProductDto;
import roman.andrusiak.test.task.dto.ProductListDto;
import roman.andrusiak.test.task.models.Product;
import roman.andrusiak.test.task.security.PersonDetails;
import roman.andrusiak.test.task.services.ProductService;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class CustomerController {
    private final ProductService productService;

    @Autowired
    public CustomerController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping()
    public ResponseEntity<?> getAllProducts(Model model, @AuthenticationPrincipal PersonDetails personDetails) {
        List<Product> products = productService.getAll();
        if (personDetails.getPerson().getRole().equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(new ProductListDto(products, true), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ProductListDto(products, false), HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") int id, Model model, @AuthenticationPrincipal PersonDetails personDetails) {
        Optional<Product> product = productService.getOneById(id);
        if (product.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (personDetails.getPerson().getRole().equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(new ProductDto(product.get(), true), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ProductDto(product.get(), false), HttpStatus.OK);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> saveProduct(@Valid @RequestBody Product product, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        productService.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") int id) {
        try {
            productService.delete(id);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<?> updateProduct(@RequestBody @Valid Product product, BindingResult bindingResult, @PathVariable("id") int id) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        try {
            productService.update(product, id);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
