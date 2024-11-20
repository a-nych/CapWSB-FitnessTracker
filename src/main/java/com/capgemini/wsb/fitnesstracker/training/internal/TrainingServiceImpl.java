package com.capgemini.wsb.fitnesstracker.training.internal;

import com.capgemini.wsb.fitnesstracker.training.api.*;
import com.capgemini.wsb.fitnesstracker.user.api.UserNotFoundException;
import com.capgemini.wsb.fitnesstracker.user.api.UserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
class TrainingServiceImpl implements TrainingService, TrainingProvider {

    private final TrainingRepository trainingRepository;
    private final UserProvider userProvider;

    @Override
    public Optional<Training> getTraining(Long trainingId) {
        return trainingRepository.findById(trainingId);
    }

    @Override
    public List<Training> findAllTrainings() {
        return trainingRepository.findAll();
    }

    @Override
    public List<Training> findTrainingsByUserId(Long userId) {
        if (userProvider.getUser(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        return trainingRepository.findByUserId(userId);
    }

    @Override
    public List<Training> findTrainingsFinishedAfter(Date date) {
        return trainingRepository.findByEndTimeAfter(date);
    }

    @Override
    public List<Training> findTrainingsByActivityType(String activityType) {
        try {
            ActivityType type = ActivityType.valueOf(activityType.toUpperCase());
            return trainingRepository.findByActivityType(type);
        } catch (IllegalArgumentException e) {
            throw new InvalidActivityTypeException(activityType);
        }
    }

    @Override
    public Training createTraining(Training training) {
        log.info("Creating Training {}", training);
        if (training.getId() != null) {
            throw new IllegalArgumentException("Training has already DB ID, update is not permitted!");
        }
        return trainingRepository.save(training);
    }

    @Override
    public Training updateTraining(Training training) {
        log.info("Updating Training {}", training);
        if (training.getId() == null) {
            throw new IllegalArgumentException("Training has no ID, create new training instead!");
        }
        if (!trainingRepository.existsById(training.getId())) {
            throw new TrainingNotFoundException(training.getId());
        }
        return trainingRepository.save(training);
    }

    @Override
    public void deleteTraining(final Long trainingId) {
        Training training = this.getTraining(trainingId).orElseThrow(() -> new TrainingNotFoundException(trainingId));
        trainingRepository.delete(training);
    }
}
