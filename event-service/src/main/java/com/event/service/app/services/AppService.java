package com.event.service.app.services;

import com.event.service.app.models.Event;

import java.util.List;

public interface AppService {

    List<Event> fetchEvents();

}