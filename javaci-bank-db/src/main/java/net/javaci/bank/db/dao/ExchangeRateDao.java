package net.javaci.bank.db.dao;


import net.javaci.bank.db.model.ExchangeRate;
import net.javaci.bank.db.model.enumaration.AccountCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;

@Transactional
@Repository
public interface ExchangeRateDao extends JpaRepository<ExchangeRate, Long> {

    boolean existsByDateAndCurrency(LocalDate date, AccountCurrency currenct);

}
