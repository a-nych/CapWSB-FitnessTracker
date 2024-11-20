package com.capgemini.wsb.fitnesstracker.training.api;

/**
 * Interface (API) for modifying operations on {@link Training} entities through the API.
 * Implementing classes are responsible for executing changes within a database transaction,
 * whether by continuing an existing transaction or creating a new one if required.
 */
public interface TrainingService {
    /**
     * Creates a new training
     *
     * @param training Training to create
     * @return Created training
     */
    Training createTraining(Training training);

    /**
     * Updates existing training
     *
     * @param training Training to update
     * @return Updated training
     * @throws TrainingNotFoundException if training is not found
     */
    Training updateTraining(Training training);

    void deleteTraining(Long userId);
}
