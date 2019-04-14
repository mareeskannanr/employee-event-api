package com.event.service.app;

import com.event.service.app.config.KafkaTestConfig;
import com.event.service.app.utils.AppConstants;
import com.event.service.app.utils.TestUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@EnableKafka
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {KafkaTestConfig.class})
@EmbeddedKafka(topics = AppConstants.KAFKA_TOPIC, controlledShutdown = true, brokerProperties = {"listeners=PLAINTEXT://localhost:3333", "port=3333"})
@ActiveProfiles("test")
public class ApplicationIntegrationTests {

    private static final String LOCAL_HOST = "http://localhost:";

    @LocalServerPort
    private int port;

    private URL base;

    @Value(AppConstants.KAFKA_TOPIC)
    private String topic;

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Before
    public void setUp() throws Exception {
        this.base = new URL(LOCAL_HOST + port + AppConstants.API + AppConstants.EVENTS);
    }

    @Test
    public void fetchEventsReturnsEmpty() throws Exception {
        ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);

        assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());
        JSONArray events = new JSONArray(response.getBody());
        System.out.println(events);
        assertEquals(0, events.length());
    }

    @Test
    public void fetchEventsReturn() throws Exception {
        JSONArray events = new JSONArray(TestUtils.generateEventMessage(3));
        //Asynchronous Operation so introduce thread
        kafkaTemplate.send(new ProducerRecord<>(topic, events.get(0)));
        kafkaTemplate.send(new ProducerRecord<>(topic, events.get(1)));
        kafkaTemplate.send(new ProducerRecord<>(topic, events.get(2)));
        Thread.sleep(3000);

        ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
        assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());

        JSONArray resultEvents = new JSONArray(response.getBody());
        assertTrue(resultEvents.length() > 0);
    }

}
