package com.capgemini.wsb.fitnesstracker.training.internal;

import com.capgemini.wsb.fitnesstracker.training.api.Training;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

interface TrainingRepository extends JpaRepository<Training, Long> {
    /**
     * Finds all trainings for a specific user
     *
     * @param userId ID of the user
     * @return List of trainings
     */
    default List<Training> findByUserId(Long userId) {
        return findAll().stream()
                .filter(training -> {
                    assert training.getUser().getId() != null;
                    return training.getUser().getId().equals(userId);
                })
                .toList();
    }

    /**
     * Finds all trainings finished after specified date
     *
     * @param date Date after which trainings should be found
     * @return List of trainings
     */
    default List<Training> findByEndTimeAfter(Date date) {
        return findAll().stream()
                .filter(training -> training.getEndTime().after(date))
                .toList();
    }

    /**
     * Finds all trainings of specific activity type
     *
     * @param activityType Type of activity
     * @return List of trainings
     */
    default List<Training> findByActivityType(ActivityType activityType) {
        return findAll().stream()
                .filter(training -> training.getActivityType() == activityType)
                .toList();
    }
}
