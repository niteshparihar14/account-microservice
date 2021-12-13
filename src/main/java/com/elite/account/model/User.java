package com.elite.account.model;

import lombok.Data;

@Data
public class User {

	private Long id;

	private String name;

	private String emailId;

	private String phone;

	private String password;

	private String role;

	private int status;
}
