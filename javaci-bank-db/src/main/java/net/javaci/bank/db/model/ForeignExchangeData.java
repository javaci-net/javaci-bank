package net.javaci.bank.db.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class ForeignExchangeData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
}
