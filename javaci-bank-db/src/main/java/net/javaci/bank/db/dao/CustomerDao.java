package net.javaci.bank.db.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.javaci.bank.db.model.Customer;

import java.util.Optional;

@Transactional
@Repository
public interface CustomerDao extends JpaRepository<Customer, Long> {

    public Optional<Customer> findByCitizenNumber(String citizenNumber);

}
