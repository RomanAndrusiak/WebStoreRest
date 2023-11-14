package roman.andrusiak.test.task.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;
    @Column(name = "name")
    @Size(min = 2, max = 50, message = "length of name should be more than 2 symbols")
    String name;
    @Column(name = "description")
    @NotEmpty(message = "Description should not be empty")
    String description;
    @Min(value = 0, message = "Count less must be minimal 0")
    @Column(name = "count_left")
    int countLeft;
    @Min(value = 0, message = "Price must be minimal 0")
    @Column(name = "price")
    private double price;
    @Column(name = "photo", columnDefinition = "bytea")
    private byte[] photo;


}