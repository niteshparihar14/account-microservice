package com.elite.account.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoanRequest {

	private Long loanId; 
	
	private Long accountId;
	
	private BigDecimal loanAmount;
	
	private int tenure;
	
	private String status;
}
