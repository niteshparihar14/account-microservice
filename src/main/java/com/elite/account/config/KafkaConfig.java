package com.elite.account.config;

public class KafkaConfig {
	public final static String applicationID = "Producer-Account";
	public final static String bootstrapServers = "localhost:9092";
	public final static String topicName = "account-topic-1";
	public final static int numEvents = 10;
}
