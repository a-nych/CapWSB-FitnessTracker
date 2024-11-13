package com.capgemini.wsb.fitnesstracker.training.api;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TrainingProvider {
    /**
     * Retrieves a training based on their ID.
     * If the training with given ID is not found, then {@link Optional#empty()} will be returned.
     *
     * @param trainingId id of the training to be searched
     * @return An {@link Optional} containing the located Training, or {@link Optional#empty()} if not found
     */
    Optional<Training> getTraining(Long trainingId);

    /**
     * Retrieves all trainings.
     *
     * @return List of all trainings
     */
    List<Training> findAllTrainings();

    /**
     * Retrieves all trainings for a specific user.
     *
     * @param userId ID of the user
     * @return List of trainings for the user
     */
    List<Training> findTrainingsByUserId(Long userId);

    /**
     * Retrieves all trainings finished after specified date
     *
     * @param date Date after which trainings should be found
     * @return List of trainings
     */
    List<Training> findTrainingsFinishedAfter(Date date);

    /**
     * Retrieves all trainings by activity type
     *
     * @param activityType Type of activity
     * @return List of trainings
     */
    List<Training> findTrainingsByActivityType(String activityType);
}
