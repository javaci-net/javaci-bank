package net.javaci.bank.db.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.javaci.bank.db.model.Account;
import net.javaci.bank.db.model.Customer;

@Transactional
@Repository
public interface AccountDao extends JpaRepository<Account, Long> {
	
	int countByCustomerId(Long customerId);

	List<Account> findAllByCustomer(Customer customer);
	
	Page<Account> findAllByCustomer(Customer customer, Pageable pageable);

}