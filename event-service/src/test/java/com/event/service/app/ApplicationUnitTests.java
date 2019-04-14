package com.event.service.app;

import com.event.service.app.models.Event;
import com.event.service.app.repositories.EventRepository;
import com.event.service.app.services.KafkaConsumerService;
import com.event.service.app.utils.AppConstants;
import com.event.service.app.utils.TestUtils;
import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(topics = AppConstants.KAFKA_TOPIC, controlledShutdown = true, brokerProperties = {"listeners=PLAINTEXT://localhost:3333", "port=3333"})
@ActiveProfiles("test")
public class ApplicationUnitTests {

	@MockBean
	private EventRepository eventRepository;

	@Autowired
	private KafkaConsumerService kafkaConsumerService;

	@Autowired
	private MockMvc mockMvc;

	@Test(expected = Exception.class)
	public void checkSaveThrowsException() throws Exception {
		doThrow(new IOException()).when(eventRepository).save(any(Event.class));
		kafkaConsumerService.save(TestUtils.generateEventMessage(1));
	}

	@Test
	public void checkSave() throws Exception {
		doReturn(TestUtils.generateEventList(1).get(0)).when(eventRepository).save(any(Event.class));
		kafkaConsumerService.save(TestUtils.generateEventMessage(1));
	}

	@Test
	public void fetchEventsReturnsEmpty() throws Exception {
		doReturn(TestUtils.generateEventList(0)).when(eventRepository).findAll();
		MvcResult result = mockMvc.perform(get(AppConstants.API + AppConstants.EVENTS)).andReturn();

		assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
		JSONArray events = new JSONArray(result.getResponse().getContentAsString());
		assertEquals(0, events.length());
	}

	@Test
	public void fetchEventsReturnsValue() throws Exception {
		doReturn(TestUtils.generateEventList(5)).when(eventRepository).findAll();
		MvcResult result = mockMvc.perform(get(AppConstants.API + AppConstants.EVENTS)).andReturn();

		assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
		JSONArray events = new JSONArray(result.getResponse().getContentAsString());
		assertEquals(5, events.length());
	}

}
