package net.javaci.bank.db.model;

import static javax.persistence.CascadeType.PERSIST;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Customer extends UserEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String address;
    
    private LocalDate birthDate;
    
    @OneToMany(
            mappedBy = "customer",
            cascade = PERSIST,
            orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();

}
