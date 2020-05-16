package net.javaci.bank.db.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.javaci.bank.db.model.Account;

@Transactional
@Repository
public interface AccountDao extends JpaRepository<Account, Long> {
	
	int countByCustomerId(Long customerId);

}