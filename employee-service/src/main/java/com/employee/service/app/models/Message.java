package com.employee.service.app.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Message {

    private UUID uuid;

    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime time = LocalDateTime.now();

    private Action action;

    public Message(UUID uuid, Action action) {
        this.uuid = uuid;
        this.action = action;
    }

}

