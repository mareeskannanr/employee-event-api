package com.event.service.app.services;

import com.event.service.app.models.Event;
import com.event.service.app.repositories.EventRepository;
import com.event.service.app.utils.AppConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    private final Logger logger = LoggerFactory.getLogger(KafkaConsumerServiceImpl.class);

    private final EventRepository eventRepository;

    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaConsumerServiceImpl(EventRepository eventRepository, ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @KafkaListener(topics = AppConstants.KAFKA_TOPIC, groupId = AppConstants.GROUP_ID)
    public void save(String message) {
        try {
            logger.info(message);
            Event event = objectMapper.readValue(message, Event.class);
            eventRepository.save(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}