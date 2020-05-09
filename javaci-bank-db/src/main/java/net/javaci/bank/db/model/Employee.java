package net.javaci.bank.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import net.javaci.bank.db.model.enumaration.EmployeeStatusType;

@Entity
@Getter @Setter
public class Employee extends UserEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeStatusType status;
    
}
