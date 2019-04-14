package com.employee.service.app.utils;

import com.employee.service.app.models.Action;
import com.employee.service.app.models.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

public class CommonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String generateMessage(UUID uuid, Action action) {
        try {
            Message message = new Message(uuid, action);
            return mapper.writeValueAsString(message);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
