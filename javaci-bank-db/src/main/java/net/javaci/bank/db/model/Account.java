package net.javaci.bank.db.model;

import java.math.BigDecimal;
import java.util.Currency;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.sun.istack.NotNull;

import lombok.Getter;
import lombok.Setter;
import net.javaci.bank.db.converter.CurrencyConverter;

@Entity
@Getter @Setter
public class Account {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "account_id", nullable = false)
    private Long id;
	
	@NotNull
	@Column(name = "account_name", nullable = false)
	private String accountName;
	
	@Column(name = "description", nullable = true)
	private String description;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "account_type", nullable = false)
	private AccountType accountType;
	
	@Convert(converter = CurrencyConverter.class)
	@Column(name = "currency_code", nullable = false)
	private Currency currency;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_fk", nullable = false)
	private Customer customer;
	
	@Column(name = "balance", nullable = false)
	private BigDecimal balance;
	
	
}
