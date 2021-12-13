package com.elite.account.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.elite.account.model.User;

@Component
public class WebClientHandler {
	
	@Autowired
	private WebClient client;

	public User getCustomer(Long id) {
		User user = client.get().uri("lb://CUSTOMER-SERVICE/api/v1//user/" + id).retrieve()
				.bodyToMono(User.class).block();

		if (user == null) {
			throw new RuntimeException("Invalid userId");
		}
		return user;
	}
}
