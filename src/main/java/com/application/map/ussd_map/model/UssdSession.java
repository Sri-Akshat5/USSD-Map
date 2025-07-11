package com.application.map.ussd_map.model;

import lombok.Data;

import java.util.List;

@Data
public class UssdSession {
    private String sessionId;
    private String phoneNumber;
    private String fromLocation;
    private String toLocation;
    private int step;
    private boolean started;

    private List<String> directionSteps;
    private int currentStepIndex = 0;
}
