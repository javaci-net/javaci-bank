package net.javaci.bank.db.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter @Setter
public abstract class UserEntityBase {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String citizenNumber;
    
    private String name;

    @Column(nullable = true)
    private String middleName;

    private String lastName;
    
    private LocalDate birthDate;
    
    private String email;
    
    @Column(nullable = true)
    private String phoneNumber;
    
    private String password;
    
}
