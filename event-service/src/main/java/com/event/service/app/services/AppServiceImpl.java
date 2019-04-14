package com.event.service.app.services;

import com.event.service.app.models.Event;
import com.event.service.app.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class AppServiceImpl implements AppService {

    private final EventRepository eventRepository;

    @Autowired
    public AppServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> fetchEvents() {
        return eventRepository.findAll();
    }
}
