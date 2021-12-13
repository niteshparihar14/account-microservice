package com.elite.account.model;

import java.math.BigDecimal;

import com.elite.account.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionRequest {

	private Long accountId;

	private TransactionType transactionType;

	private BigDecimal amount;
	
	private Long from;
}
