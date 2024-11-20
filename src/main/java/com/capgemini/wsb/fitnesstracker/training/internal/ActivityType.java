package com.capgemini.wsb.fitnesstracker.training.internal;

import lombok.Getter;

// TODO : JavaDoc
@Getter
public enum ActivityType {

    RUNNING("Running"),
    CYCLING("Cycling"),
    WALKING("Walking"),
    SWIMMING("Swimming"),
    TENNIS("Tenis");

    private final String displayName;

    ActivityType(String displayName) {
        this.displayName = displayName;
    }

}
