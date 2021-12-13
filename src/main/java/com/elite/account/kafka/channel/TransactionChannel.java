package com.elite.account.kafka.channel;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

@SuppressWarnings("deprecation")
public interface TransactionChannel {

	String OUTPUT = "transaction-out";
	
	String OUT = "account-out";

	@Output(OUTPUT)
	MessageChannel outboundTransation();
	
	@Output(OUT)
	MessageChannel outboundAccount();

}
