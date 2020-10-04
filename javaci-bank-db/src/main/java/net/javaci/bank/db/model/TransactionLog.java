package net.javaci.bank.db.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;
import net.javaci.bank.db.model.enumaration.TransactionLogType;

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
    private Long id;
    
    /** The id of the account that the transaction belongs to. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_fk", nullable = false)
    private Account account;
    
    /** The id of the account that the transaction belongs to. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_to_fk", nullable = false)
    private Account toAccount;
    
    /** The signed amount of the transaction */
    @Column(nullable = false)
    private BigDecimal amount;

    /** The signed balance of the transaction */
    @Column(nullable = false)
    private BigDecimal balance;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionLogType type;
    
    @Column(nullable = false)
    private LocalDate date;
    
    /** The unstructured transaction description as it appears on the bank statement. */
    private String description;
}
