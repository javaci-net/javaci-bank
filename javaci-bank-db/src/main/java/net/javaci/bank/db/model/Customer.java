package net.javaci.bank.db.model;

import static javax.persistence.CascadeType.PERSIST;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;
import net.javaci.bank.db.model.enumaration.CustomerStatusType;

@Entity
@Getter @Setter
public class Customer extends UserEntityBase {
	
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatusType status;
    
    @OneToMany(
            mappedBy = "customer",
            cascade = PERSIST,
            orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();

}
