package com.elite.account.service;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.elite.account.entity.Account;
import com.elite.account.entity.Transaction;
import com.elite.account.repository.AccountRepository;
import com.elite.account.repository.TransactionRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountService {

	private AccountRepository accountRepository;
	
	private TransactionRepository transactionRepository;
	
	public Account fetchAccountById(Long id) {
		return this.accountRepository.findById(id)
				  .orElseThrow(() -> new RuntimeException("Element with Given Id Not Found"));
	}
	
	public List<Account> fetchAccounts() {
		return accountRepository.findAll();
	}
	
	public Account addAccount(Account account) {
		return this.accountRepository.save(account);
	}
	
	public Account updateAccount(Account account) {
		return this.accountRepository.save(account);
	}
	
	public Transaction addTransaction(Transaction transaction) {
		return this.transactionRepository.save(transaction);
	}

	public List<Transaction> fetchStatementByAccountId(Long accountId) {
		
		return this.transactionRepository.findByAccountId(accountId);
	}
	
	public List<Transaction> fetchStatementByAccountId(Long accountId, LocalDateTime startDate, LocalDateTime endDate,
			String sort, String order) {

		if (startDate != null && endDate != null) {
			if (StringUtils.isEmpty(sort) || StringUtils.isEmpty(order)) {
				sort = "id";
				order = "ASC";
			}
			return this.transactionRepository.findByAccountIdAndTransactionTimeBetween(accountId, startDate, endDate, sortBy(sort, order));
		}
		return this.transactionRepository.findByAccountId(accountId);
	}
	
	private Sort sortBy(String sort, String order) {
		if("DESC".equals(order)) {
			return Sort.by(Sort.Direction.DESC, sort);
		}
		return Sort.by(Sort.Direction.ASC, sort);
    }
}
