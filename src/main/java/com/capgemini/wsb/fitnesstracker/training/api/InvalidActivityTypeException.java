package com.capgemini.wsb.fitnesstracker.training.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidActivityTypeException extends IllegalArgumentException {
    public InvalidActivityTypeException(String activityType) {
        super("Invalid activity type: " + activityType + ". Available types are: " +
                java.util.Arrays.toString(com.capgemini.wsb.fitnesstracker.training.internal.ActivityType.values()));
    }
}
