package com.elite.account.event;

import lombok.Data;

@Data
public class LoanAccountEvent {

	private Long loanId;
	
	private Long customerId;
	
	private Status status;
	
	public enum Status{
		SUCCESS, FAILURE
	}

	public LoanAccountEvent(Long loanId, Long customerId, Status status) {
		super();
		this.loanId = loanId;
		this.customerId = customerId;
		this.status = status;
	}
}
