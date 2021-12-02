package com.elite.account.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.elite.account.entity.Account;
import com.elite.account.entity.Transaction;
import com.elite.account.enums.TransactionStatus;
import com.elite.account.enums.TransactionType;
import com.elite.account.model.Notification;
import com.elite.account.service.AccountService;
import com.elite.account.utils.Producer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@RestController
@RequestMapping(path = "/api/v1")
public class AccountController {

	@Autowired
	private AccountService service;

	@Autowired
	private Producer producer;

	private static ObjectMapper mapper = new ObjectMapper();

	@GetMapping(path = "/accounts/{id}")
	public ResponseEntity<Account> getAccount(@PathVariable("id") Long id) {

		Account account = this.service.fetchAccountById(id);

		if (account == null) {
			throw new RuntimeException("Account not found");
		}

		return ResponseEntity.ok().body(account);
	}

	@GetMapping(path = "/accounts")
	public ResponseEntity<List<Account>> getAllAccount() {

		List<Account> account = this.service.fetchAccounts();

		if (account == null) {
			throw new RuntimeException("Account not found");
		}

		return ResponseEntity.ok().body(account);
	}

	@PostMapping(path = "/account")
	public ResponseEntity<Account> addEntity(@RequestBody Account account) throws JsonProcessingException {

		Account addedObject = this.service.addAccount(account);

		if (addedObject != null) {
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
					.buildAndExpand(account.getId()).toUri();

			mapper.registerModule(new JavaTimeModule());

			Notification notification = new Notification();
			notification.setCustomerId(addedObject.getCustomerId());
			notification.setMessage("Account created successfully");
			producer.kafkaProducer(notification);

			return ResponseEntity.created(location).body(account);
		} else {
			throw new RuntimeException("Resource With Id Already Exisits");
		}
	}

	@PostMapping(path = "/account/transaction")
	public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction transaction)
			throws JsonProcessingException {

		LocalDateTime now = LocalDateTime.now();
		String transactionRefId = RandomStringUtils.randomAlphanumeric(12);
		transaction.setTransactionTime(now);
		transaction.setTransactionRefId(transactionRefId);

		Account account = service.fetchAccountById(transaction.getAccountId());

		if (account == null) {
			throw new RuntimeException("Invalid account id");
		}

		transaction.setStatus(TransactionStatus.SUCCESS);

		BigDecimal updatedBalance = null;

		if (TransactionType.CREDIT.equals(transaction.getTransactionType())) {
			updatedBalance = account.getBalance().add(transaction.getAmount());
		} else if (TransactionType.DEBIT.equals(transaction.getTransactionType())
				&& account.getBalance().compareTo(transaction.getAmount()) == 1) {
			updatedBalance = account.getBalance().subtract(transaction.getAmount());
		} else {
			throw new RuntimeException("Low account balance");
		}

		Transaction addedObject = this.service.addTransaction(transaction);

		if (addedObject == null) {
			throw new RuntimeException("Transaction failed");
		}

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(transaction.getId()).toUri();

		mapper.registerModule(new JavaTimeModule());

		Notification notification = new Notification();
		notification.setCustomerId(account.getCustomerId());
		notification.setMessage(transaction.getTransactionType() + " transaction success");
		
		producer.kafkaProducer(notification);
		
		account.setBalance(updatedBalance);

		if(service.updateAccount(account) == null) {
			throw new RuntimeException("Failed to update account");
		}

		return ResponseEntity.created(location).body(transaction);
	}
	
	@GetMapping(path = "/accounts/{id}/statement")
	public ResponseEntity<List<Transaction>> getStatementByAccountId(@PathVariable("id") Long id,
			@RequestParam(required = false)  @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime startTime,
			@RequestParam(required = false)  @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime endTime,
			@RequestParam(required = false)  String sort,
			@RequestParam(required = false)  String sortOrder) {

		List<Transaction> statements = this.service.fetchStatementByAccountId(id, startTime, endTime, sort, sortOrder);

		if (CollectionUtils.isEmpty(statements)) {
			throw new RuntimeException("Statement not found");
		}

		return ResponseEntity.ok().body(statements);
	}
}
