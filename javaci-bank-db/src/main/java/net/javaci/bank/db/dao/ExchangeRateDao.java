package net.javaci.bank.db.dao;


import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.javaci.bank.db.model.ExchangeRate;
import net.javaci.bank.db.model.enumaration.AccountCurrency;

@Transactional
@Repository
public interface ExchangeRateDao extends JpaRepository<ExchangeRate, Long> {

    boolean existsByDateAndCurrency(LocalDate date, AccountCurrency currenct);
    
    List<ExchangeRate> findAllByDate(LocalDate date);

}
