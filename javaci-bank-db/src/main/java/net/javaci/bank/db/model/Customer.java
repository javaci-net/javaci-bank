package net.javaci.bank.db.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.javaci.bank.db.model.enumaration.CustomerStatusType;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.PERSIST;

@Entity
@Getter @Setter @NoArgsConstructor
public class Customer extends UserEntityBase {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatusType status;

    @OneToMany(
            mappedBy = "customer",
            cascade = PERSIST,
            orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();

    @Builder
    public Customer(Long id, String citizenNumber, String firstName, String middleName, String lastName, LocalDate birthDate,
                    String email, String phoneNumber, String password, CustomerStatusType status) {
        super(id, citizenNumber, firstName, middleName, lastName, birthDate, email, phoneNumber, password);
        this.status = status;
    }
}
