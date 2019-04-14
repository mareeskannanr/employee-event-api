package com.event.service.app.utils;

import com.event.service.app.models.Action;
import com.event.service.app.models.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestUtils {

    private static ObjectMapper mapper = new ObjectMapper();

    public static List<Event> generateEventList(int length) {

        List<Event> eventList = new ArrayList<>();
        for(int i=1; i<=length; i++) {
            Event event = new Event();
            event.setId((long) i);
            event.setUuid(UUID.randomUUID());
            event.setAction(Action.values()[i % 3]);
            eventList.add(event);
        }

        return eventList;
    }

    public static String generateEventMessage(int length) throws Exception {

        JSONArray eventArray = new JSONArray();
        List<Event> events = generateEventList(length);

        events.forEach(event -> {
            try {
                eventArray.put(mapper.writeValueAsString(event));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

        if(length == 1) {
            return eventArray.get(0).toString();
        }

        return eventArray.toString();
    }

}
