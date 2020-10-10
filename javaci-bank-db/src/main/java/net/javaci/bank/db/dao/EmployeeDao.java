package net.javaci.bank.db.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.javaci.bank.db.model.Employee;

@Transactional
@Repository
public interface EmployeeDao extends JpaRepository<Employee, Long> {

	boolean existsByCitizenNumber(String citizenNumber);

	Employee findByEmail(String email);

}