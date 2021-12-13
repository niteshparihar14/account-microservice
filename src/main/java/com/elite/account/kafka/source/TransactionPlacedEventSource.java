package com.elite.account.kafka.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import com.elite.account.entity.Transaction;
import com.elite.account.event.LoanAccountEvent;
import com.elite.account.event.TransactionEvent;
import com.elite.account.kafka.channel.TransactionChannel;
import com.elite.account.model.User;

@Component
public class TransactionPlacedEventSource {

	@Autowired
	private TransactionChannel transactionChannel;

	public void publishTransactionEvent(Transaction transaction, User user) {

		TransactionEvent message = new TransactionEvent();
		message.setCustomerId(user.getId());
		message.setMessage("Transaction success for " +transaction.getTransactionType().toString().toLowerCase());
		message.setAction(TransactionEvent.Action.TRANSACTION_PUT);
		message.setStatus("success");
		message.setEmail(user.getEmailId());
		message.setPhone(user.getPhone());
		
		MessageChannel messageChannel = transactionChannel.outboundTransation();
		messageChannel.send(MessageBuilder.withPayload(message)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
				.build());
	}
	
	public void publishAccountEvent(LoanAccountEvent message) {
		MessageChannel messageChannel = transactionChannel.outboundAccount();
		messageChannel.send(MessageBuilder.withPayload(message)
				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
				.build());
	}

}
