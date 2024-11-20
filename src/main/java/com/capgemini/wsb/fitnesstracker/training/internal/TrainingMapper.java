package com.capgemini.wsb.fitnesstracker.training.internal;

import com.capgemini.wsb.fitnesstracker.training.api.Training;
import com.capgemini.wsb.fitnesstracker.user.api.User;
import com.capgemini.wsb.fitnesstracker.user.api.UserProvider;
import org.springframework.stereotype.Component;

@Component
class TrainingMapper {

    private final UserProvider userProvider;

    public TrainingMapper(UserProvider userProvider) {
        this.userProvider = userProvider;
    }

    TrainingDto toDto(Training training) {
        TrainingDto dto = new TrainingDto();
        dto.setId(training.getId());
        dto.setStartTime(training.getStartTime());
        dto.setEndTime(training.getEndTime());
        dto.setActivityType(training.getActivityType());
        dto.setDistance(training.getDistance());
        dto.setAverageSpeed(training.getAverageSpeed());
        dto.setUser(training.getUser());
        return dto;
    }

    Training toEntity(TrainingCreateDto dto) {
        User user = userProvider.getUser(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + dto.getUserId()));

        return new Training(
                user,
                dto.getStartTime(),
                dto.getEndTime(),
                dto.getActivityType(),
                dto.getDistance(),
                dto.getAverageSpeed()
        );
    }

    Training toUpdatedEntity(Training existingTraining, TrainingUpdateDto updateDto) {
        User user = updateDto.getUserId() != null ?
                userProvider.getUser(updateDto.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + updateDto.getUserId()))
                : existingTraining.getUser();

        return new Training(
                user,
                updateDto.getStartTime() != null ? updateDto.getStartTime() : existingTraining.getStartTime(),
                updateDto.getEndTime() != null ? updateDto.getEndTime() : existingTraining.getEndTime(),
                updateDto.getActivityType() != null ? updateDto.getActivityType() : existingTraining.getActivityType(),
                updateDto.getDistance() != null ? updateDto.getDistance() : existingTraining.getDistance(),
                updateDto.getAverageSpeed() != null ? updateDto.getAverageSpeed() : existingTraining.getAverageSpeed()
        );
    }
}
