package net.javaci.bank.db.model;

import java.time.LocalDate;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter @Setter @ToString
public abstract class UserEntityBase {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String citizenNumber;
    
    private String name;

    @Column(nullable = true)
    private String middleName;

    private String lastName;
    
    private LocalDate birthDate;
    
    private String email;
    
    @Column(nullable = true)
    private String phoneNumber;

    @ToString.Exclude
    private transient String password;
    
}
