package com.bibloteca.beta.entities;

import com.bibloteca.beta.enums.Role;
import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.OneToMany;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@Entity
<<<<<<< HEADasklhgf
=======
@Data
@AllArgsConstructor
@NoArgsConstructor
>>>>>>> 8ac0af87a2eb6ee505a88673d0cd52934a2efda4
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Author implements Serializable {//implements serializable??

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String name;
    private String lastName;
    private String email;
    private String password;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Photo photo;

    @EqualsAndHashCode.Include
    private Integer dni;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean alive;

    //mappedBY(name = 'author')
    private ArrayList<Book> listBook;

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                // ⚠️ No ponemos password ni photo
                '}';
    }
}
