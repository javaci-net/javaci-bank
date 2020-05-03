package net.javaci.bank.db.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.javaci.bank.db.model.TransactionLog;

@Transactional
@Repository
public interface TransactionLogDao extends JpaRepository<TransactionLog, Long> {

}