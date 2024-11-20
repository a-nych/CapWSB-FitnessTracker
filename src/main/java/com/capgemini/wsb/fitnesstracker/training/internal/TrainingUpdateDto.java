package com.capgemini.wsb.fitnesstracker.training.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TrainingUpdateDto {
    @Nullable
    private Long userId;

    @Nullable
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private Date startTime;

    @Nullable
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private Date endTime;

    @Nullable
    private ActivityType activityType;

    @Nullable
    private Double distance;

    @Nullable
    private Double averageSpeed;
}
