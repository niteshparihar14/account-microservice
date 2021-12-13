package com.elite.account.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
import com.elite.account.enums.TransactionType;
import com.elite.account.event.LoanAccountEvent;
import com.elite.account.event.LoanAccountEvent.Status;
import com.elite.account.event.TransactionEvent;
import com.elite.account.kafka.source.TransactionPlacedEventSource;
import com.elite.account.model.LoanRequest;
import com.elite.account.model.TransactionRequest;
import com.elite.account.model.User;
import com.elite.account.service.AccountService;
import com.elite.account.utils.WebClientHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@RestController
@RequestMapping(path = "/api/v1")
@Transactional(rollbackFor = { Exception.class })
public class AccountController {

	@Autowired
	private AccountService service;

	@Autowired
	private TransactionPlacedEventSource transactionPlacedEventSource;

	@Autowired
	private WebClientHandler client;

	private static ObjectMapper mapper = new ObjectMapper();

	/**
	 * @param id
	 * @return
	 * @apiNote API to fetch account details by accountId
	 */
	@GetMapping(path = "/accounts/{id}")
	public ResponseEntity<Account> getAccount(@PathVariable("id") Long id) {

		Account account = this.service.fetchAccountById(id);

		if (account == null) {
			throw new RuntimeException("Account not found");
		}

		return ResponseEntity.ok().body(account);
	}

	@PostMapping(path = "/account/validate")
	public ResponseEntity<Account> validateLoanRequest(@RequestBody LoanRequest request) {

		Account account = this.service.fetchAccountById(request.getAccountId());

		LoanAccountEvent event = null;
		if (account == null || account.getBalance().compareTo(new BigDecimal(200000.00)) == -1) {
			event = new LoanAccountEvent(request.getLoanId(), account.getCustomerId(), Status.FAILURE);
			transactionPlacedEventSource.publishAccountEvent(event);
			return ResponseEntity.ok().body(account);
		}

		event = new LoanAccountEvent(request.getLoanId(), account.getCustomerId(), Status.SUCCESS);
		transactionPlacedEventSource.publishAccountEvent(event);

		return ResponseEntity.ok().body(account);
	}

	/**
	 * @return
	 * @apiNote API to fetch all account details
	 */
	@GetMapping(path = "/accounts")
	public ResponseEntity<List<Account>> getAllAccount() {

		List<Account> account = this.service.fetchAccounts();

		if (account == null) {
			throw new RuntimeException("Account not found");
		}

		return ResponseEntity.ok().body(account);
	}

	/**
	 * @param account
	 * @return
	 * @throws JsonProcessingException
	 * @apiNote API to perform add account
	 */
	@PostMapping(path = "/account")
	public ResponseEntity<Account> addEntity(@RequestBody Account account) throws JsonProcessingException {

		Account addedObject = this.service.addAccount(account);

		if (addedObject != null) {
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
					.buildAndExpand(account.getId()).toUri();

			mapper.registerModule(new JavaTimeModule());

			TransactionEvent notification = new TransactionEvent();
			notification.setCustomerId(addedObject.getCustomerId());
			notification.setMessage("Account created successfully");

			return ResponseEntity.created(location).body(account);
		} else {
			throw new RuntimeException("Resource With Id Already Exisits");
		}
	}

	/**
	 * @param transaction
	 * @return
	 * @throws JsonProcessingException
	 * @apiNote API to perform credit/debit
	 */
	@PostMapping(path = "/account/transaction")
	public ResponseEntity<Transaction> addTransaction(@RequestBody TransactionRequest request)
			throws JsonProcessingException {

		Account account = service.fetchAccountById(request.getAccountId());

		if (account == null) {
			throw new RuntimeException("Invalid account id");
		}

		BigDecimal updatedBalance = null;

		if (TransactionType.CREDIT.equals(request.getTransactionType())) {
			updatedBalance = account.getBalance().add(request.getAmount());
		} else if (TransactionType.DEBIT.equals(request.getTransactionType())
				&& account.getBalance().compareTo(request.getAmount()) == 1) {
			updatedBalance = account.getBalance().subtract(request.getAmount());
		} else {
			throw new RuntimeException("Low account balance");
		}

		User user = client.getCustomer(account.getCustomerId());

		Transaction addedTransaction = this.service.addTransaction(request);

		if (addedTransaction == null) {
			throw new RuntimeException("Transaction failed");
		}

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(addedTransaction.getId()).toUri();

		transactionPlacedEventSource.publishTransactionEvent(addedTransaction, user);

		account.setBalance(updatedBalance);

		if (service.updateAccount(account) == null) {
			throw new RuntimeException("Failed to update account");
		}

		return ResponseEntity.created(location).body(addedTransaction);
	}

	/**
	 * @param id
	 * @param startTime
	 * @param endTime
	 * @param sort
	 * @param sortOrder
	 * @return
	 * @apiNote API to fetch the statement
	 */
	@GetMapping(path = "/accounts/{id}/statement")
	public ResponseEntity<List<Transaction>> getStatementByAccountId(@PathVariable("id") Long id,
			@RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime startTime,
			@RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime endTime,
			@RequestParam(required = false) String sort, @RequestParam(required = false) String sortOrder) {

		List<Transaction> statements = this.service.fetchStatementByAccountId(id, startTime, endTime, sort, sortOrder);

		if (CollectionUtils.isEmpty(statements)) {
			throw new RuntimeException("Statement not found");
		}

		return ResponseEntity.ok().body(statements);
	}
}
