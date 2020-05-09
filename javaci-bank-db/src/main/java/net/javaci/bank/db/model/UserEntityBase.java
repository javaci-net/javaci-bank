package net.javaci.bank.db.model;

import java.time.LocalDate;

import javax.persistence.Column;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class UserEntityBase {

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
