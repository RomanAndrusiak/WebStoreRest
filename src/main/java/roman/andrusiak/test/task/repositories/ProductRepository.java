package roman.andrusiak.test.task.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import roman.andrusiak.test.task.models.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
