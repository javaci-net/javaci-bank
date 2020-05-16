package net.javaci.bank.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.Getter;
import lombok.Setter;
import net.javaci.bank.db.model.enumaration.EmployeeStatusType;

@Entity
@Getter @Setter
public class Employee extends UserEntityBase {
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeStatusType status;
    
}
