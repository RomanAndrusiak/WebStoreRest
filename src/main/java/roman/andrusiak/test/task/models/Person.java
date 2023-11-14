package roman.andrusiak.test.task.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Person")
public class Person implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotEmpty(message = "Username should not be empty")
    @Size(min = 2, max = 30, message = "Username length should be from 2 to 30 symbols")
    @Column(name = "username")
    private String username;
    @NotEmpty(message = "First name should not be empty")
    @Size(min = 2, max = 15, message = "First name length should be from 2 to 15 symbols")
    @Column(name = "first_name")
    private String firstName;
    @NotEmpty(message = "Last name should not be empty")
    @Size(min = 2, max = 15, message = "Last name length should be from 2 to 15 symbols")
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "role")
    private String role;

}