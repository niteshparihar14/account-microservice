package com.elite.account.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.elite.account.enums.TransactionStatus;
import com.elite.account.enums.TransactionType;

import lombok.Data;

@Entity
@Table(name = "transaction_table")

@Data
public class Transaction {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "accountid")
	private Long accountId;

	@Column(name = "transactiontype")
	private TransactionType transactionType;

	@Column(name = "transactionrefId")
	private String transactionRefId;

	@Column(name = "amount")
	private BigDecimal amount;

	@Column(name = "from")
	private Long from;
	
	@Column(name = "transactiontime")
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private LocalDateTime transactionTime;
	
	@Column(name = "status")
	private TransactionStatus status;
}
