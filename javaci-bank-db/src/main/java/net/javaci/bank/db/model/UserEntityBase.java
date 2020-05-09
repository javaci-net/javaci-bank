package net.javaci.bank.db.model;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.sun.istack.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class UserEntityBase {

    private String name;

    private String middleName;

    private String lastName;
    
    private String identityNo;
    
    private String email;
    
    private String password;
    
    @Column(name = "description", nullable = true)
    private String phoneNumber;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusType status;
}
