package com.event.service.app.utils;

public class AppConstants {

    public static final String KAFKA_TOPIC = "${app.kafka.topic}";
    public static final String GROUP_ID = "${app.kafka.group-id}";

    public static final String API = "/api/";
    public static final String EVENTS = "events";

    public static final String TITLE = "Event Service API";
    public static final String DESCRIPTION = "Capturing Actions Performed In Employee Service & Expose An Endpoint To View All Event Captured";
    public static final String VERSION = "1.0.0";
    public static final String CTRL_PACKAGE = "com.event.service.app.controllers";
    public static final String SWAGGER_ENDPOINT = API + ".*";
    public static final String EVENTS_TAG = "Events";
    public static final String CTRL_DESCRIPTION = "REST API for Events";
    public static final String LIST = "List";
    public static final String FETCH_VALUE  = "Fetches all events captured during Employee-Service operations";

}