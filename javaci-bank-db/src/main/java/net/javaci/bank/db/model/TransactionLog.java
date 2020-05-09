package net.javaci.bank.db.model;

import java.math.BigDecimal;
import java.time.LocalDate;
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

/**
 * The Transactions resource represents the known transactions on the account.
 * Each individual Transaction resource represents an individual transaction on
 * the account either posted or pending.
 */
@Entity
@Getter @Setter
public class TransactionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "account_id", nullable = false)
    private Long id;
    
    /** The id of the account that the transaction belongs to. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_fk", nullable = false)
    private Account account;
    
    /** The signed amount of the transaction */
    @NotNull
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    
    /** The <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO 8601</a> currency code of the account. */
    @Convert(converter = CurrencyConverter.class)
    @Column(name = "currency_code", nullable = false)
    private Currency currency; 
    
    private LocalDate date;
    
    /** The unstructured transaction description as it appears on the bank statement. */
    private String description;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_log_type", nullable = false)
    private TransactionLogType transactionLogType;
}
