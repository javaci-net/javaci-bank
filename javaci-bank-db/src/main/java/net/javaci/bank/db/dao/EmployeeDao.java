package net.javaci.bank.db.dao;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.javaci.bank.db.model.Employee;

@Transactional
@Repository
public interface EmployeeDao extends JpaRepository<Employee, Long> {

	Optional<Employee> findByCitizenNumber(String citizenNumber);

}