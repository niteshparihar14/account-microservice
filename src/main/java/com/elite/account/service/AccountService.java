package com.elite.account.service;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elite.account.entity.Account;
import com.elite.account.entity.Transaction;
import com.elite.account.enums.TransactionStatus;
import com.elite.account.model.TransactionRequest;
import com.elite.account.repository.AccountRepository;
import com.elite.account.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountService {

	private AccountRepository accountRepository;
	
	private TransactionRepository transactionRepository;
	
	/**
	 * @param id
	 * @return
	 */
	public Account fetchAccountById(Long id) {
		return this.accountRepository.findById(id)
				  .orElseThrow(() -> new RuntimeException("Element with Given Id Not Found"));
	}
	
	/**
	 * @return
	 */
	public List<Account> fetchAccounts() {
		return accountRepository.findAll();
	}
	
	/**
	 * @param account
	 * @return
	 */
	@Transactional
	public Account addAccount(Account account) {
		return this.accountRepository.save(account);
	}
	
	/**
	 * @param account
	 * @return
	 */
	@Transactional
	public Account updateAccount(Account account) {
		return this.accountRepository.save(account);
	}
	
	/**
	 * @param transaction
	 * @return
	 */
	@Transactional
	public Transaction addTransaction(TransactionRequest request) {
		Account account = accountRepository.findById(request.getAccountId()).get();
		Transaction transaction = new Transaction();
		LocalDateTime now = LocalDateTime.now();
		String transactionRefId = RandomStringUtils.randomAlphanumeric(12);
		transaction.setTransactionTime(now);
		transaction.setTransactionRefId(transactionRefId);
		transaction.setStatus(TransactionStatus.SUCCESS);
		transaction.setAccountId(account.getId());
		transaction.setAmount(request.getAmount());
		transaction.setTransactionType(request.getTransactionType());
		transaction.setFrom(request.getFrom());
		return this.transactionRepository.save(transaction);
	}

	/**
	 * @param accountId
	 * @return
	 */
	public List<Transaction> fetchStatementByAccountId(Long accountId) {
		
		return this.transactionRepository.findByAccountId(accountId);
	}
	
	/**
	 * @param accountId
	 * @param startDate
	 * @param endDate
	 * @param sort
	 * @param order
	 * @return
	 */
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
	
	/**
	 * @param sort
	 * @param order
	 * @return
	 */
	private Sort sortBy(String sort, String order) {
		if("DESC".equals(order)) {
			return Sort.by(Sort.Direction.DESC, sort);
		}
		return Sort.by(Sort.Direction.ASC, sort);
    }
	
	/**
	 * @param transaction
	 * @param account
	 * @throws JsonProcessingException
	 */
	public void sendTransactionNotification(Transaction transaction, Account account ) throws JsonProcessingException {
//		ObjectMapper mapper = new ObjectMapper();
//		
//		mapper.registerModule(new JavaTimeModule());
//
//		TransactionEvent notification = new TransactionEvent();
//		notification.setCustomerId(account.getCustomerId());
//		notification.setMessage(transaction.getTransactionType() + " transaction success");
//		
//		producer.kafkaProducer(notification);
		
	}
}
