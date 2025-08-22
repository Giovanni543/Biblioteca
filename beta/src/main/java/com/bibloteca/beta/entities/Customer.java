package com.bibloteca.beta.entities;

import com.bibloteca.beta.enums.Role;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Customer implements Serializable {//que es serializable?

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

    private Boolean active;

    private Double balance;//el balance dejo que lo ingrese el propio customer o se hace directamente en el workbench de la bbdd?
    //agregarle el atributo rango
    //lista de libros comprados (historial)
    
    /*public String getFullName(){
        return (this.name + " " + this.lastName);
    }*/
}
