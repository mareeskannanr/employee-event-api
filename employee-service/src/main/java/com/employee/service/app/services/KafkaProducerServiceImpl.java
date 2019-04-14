package com.employee.service.app.services;

import com.employee.service.app.utils.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerServiceImpl.class);

    @Value(AppConstants.KAFKA_TOPIC)
    private String topic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendMessage(String message) {
        logger.info(String.format("#### -> Producing message -> %s", message));
        kafkaTemplate.send(topic, message);
    }

}
