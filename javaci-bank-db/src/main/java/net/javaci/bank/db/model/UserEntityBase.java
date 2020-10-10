package net.javaci.bank.db.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor
public abstract class UserEntityBase {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String citizenNumber;
    
    private String firstName;

    @Column(nullable = true)
    private String middleName;

    @Column(nullable = false)
    @NotEmpty
    @Size(min = 1, max = 10)
    private String lastName;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    
    private String email;
    
    @Column(nullable = true)
    private String phoneNumber;

    @Column(nullable = false)
    @ToString.Exclude
    private String password;
    
}
