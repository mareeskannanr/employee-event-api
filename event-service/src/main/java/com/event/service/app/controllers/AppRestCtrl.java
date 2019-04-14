package com.event.service.app.controllers;

import com.event.service.app.models.Event;
import com.event.service.app.services.AppService;
import com.event.service.app.utils.AppConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AppConstants.API)
@Api(value=AppConstants.EVENTS_TAG, description=AppConstants.CTRL_DESCRIPTION, tags={ AppConstants.EVENTS_TAG })
public class AppRestCtrl {

    private final AppService appService;

    @Autowired
    public AppRestCtrl(AppService appService) {
        this.appService = appService;
    }

    @GetMapping(AppConstants.EVENTS)
    @ApiOperation(value=AppConstants.FETCH_VALUE, response=Event.class, responseContainer=AppConstants.LIST)
    public ResponseEntity fetchEvents() {
        return ResponseEntity.ok(appService.fetchEvents());
    }

}
