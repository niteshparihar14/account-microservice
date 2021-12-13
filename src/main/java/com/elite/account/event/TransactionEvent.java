package com.elite.account.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEvent {

	private Long customerId;

	private String message;

	private Action action;
	
	private String status;
	
	private String email;
	
	private String phone;

	public static enum Action {
		TRANSACTION_FETCHED, TRANSACTION_PUT
	}
}
