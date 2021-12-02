package com.elite.account.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.elite.account.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	
	List<Transaction> findByAccountId(Long accountId);
	
	List<Transaction> findByAccountIdAndTransactionTimeBetween(Long accountId, LocalDateTime startDate,LocalDateTime endDate, Sort sort);

}
