package com.capgemini.wsb.fitnesstracker.training.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TrainingCreateDto {
    // Getters and Setters pozostają bez zmian
    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC") // Zmieniony format
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC") // Zmieniony format
    private Date endTime;

    private ActivityType activityType;
    private double distance;
    private double averageSpeed;

}
