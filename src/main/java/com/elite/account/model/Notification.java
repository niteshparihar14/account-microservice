package com.elite.account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

	private Long customerId;

	private String emailAddress;

	private String message;

	private String phone;
}
