package com.elite.account.utils;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.elite.account.config.KafkaConfig;
import com.elite.account.model.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class Producer {

	private static final Logger logger = LogManager.getLogger();
	
	public void kafkaProducer(Notification notification) throws JsonProcessingException {
		logger.info("Creating Kafka Producer...");
        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, KafkaConfig.applicationID);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<Integer, String> producer = new KafkaProducer<>(props);

        logger.info("Start sending User data...");
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String accountStr = mapper.writeValueAsString(notification);
        logger.info("Successfully sent message to Broker:: "+ accountStr);
        producer.send(new ProducerRecord<>(KafkaConfig.topicName, 1, accountStr));
        logger.info("Successfully sent message to Broker:: "+ accountStr);

        logger.info("Finished - Closing Kafka Producer.");
        producer.close();

	}
}
