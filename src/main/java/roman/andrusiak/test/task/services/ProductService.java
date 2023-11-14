package roman.andrusiak.test.task.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import roman.andrusiak.test.task.models.Product;
import roman.andrusiak.test.task.repositories.ProductRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Optional<Product> getOneById(int id) {
        return productRepository.findById(id).stream().findAny();
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public void update(Product product, int id) {
        Optional<Product> productOptional = getOneById(id);
        if (productOptional.isEmpty()) {
            throw new NoSuchElementException("No product found with id " + id);
        }
        Product productToBeUpdated = productOptional.get();
        productToBeUpdated.setName(product.getName());
        productToBeUpdated.setDescription(product.getDescription());
        productToBeUpdated.setCountLeft(product.getCountLeft());
        productToBeUpdated.setPrice(product.getPrice());
        productRepository.save(productToBeUpdated);
    }

    public void delete(int id) {
        productRepository.deleteById(id);
    }
}
