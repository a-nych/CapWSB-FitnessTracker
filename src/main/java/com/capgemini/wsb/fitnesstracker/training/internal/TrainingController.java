package com.capgemini.wsb.fitnesstracker.training.internal;

import com.capgemini.wsb.fitnesstracker.training.api.Training;
import com.capgemini.wsb.fitnesstracker.training.api.TrainingNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/v1/trainings")
@RequiredArgsConstructor
class TrainingController {

    private final TrainingServiceImpl trainingService;
    private final TrainingMapper trainingMapper;

    @GetMapping
    public List<TrainingDto> getAllTrainings() {
        return trainingService.findAllTrainings().stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    @GetMapping("/{userId}")
    public List<TrainingDto> getTrainingsForUser(@PathVariable Long userId) {
        return trainingService.findTrainingsByUserId(userId).stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    @GetMapping("/finished/{afterTime}")
    public List<TrainingDto> getTrainingsFinishedAfter(
            @PathVariable String afterTime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(afterTime);
            return trainingService.findTrainingsFinishedAfter(date).stream()
                    .map(trainingMapper::toDto)
                    .toList();
        } catch (ParseException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid date format. Use yyyy-MM-dd"
            );
        }
    }

    @GetMapping("/activityType")
    public List<TrainingDto> getTrainingsByActivityType(
            @RequestParam String activityType) {
        if (activityType == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Activity type must be provided"
            );
        }
        return trainingService.findTrainingsByActivityType(activityType).stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrainingDto createTraining(@RequestBody TrainingCreateDto trainingDto) {
        Training training = trainingMapper.toEntity(trainingDto);
        Training createdTraining = trainingService.createTraining(training);
        return trainingMapper.toDto(createdTraining);
    }

    @PutMapping("/{trainingId}")
    public TrainingDto updateTraining(
            @PathVariable Long trainingId,
            @RequestBody TrainingCreateDto trainingDto) {
        Training existingTraining = trainingService.getTraining(trainingId)
                .orElseThrow(() -> new TrainingNotFoundException(trainingId));

        Training updatedTraining = trainingMapper.toUpdatedEntity(existingTraining, trainingDto);
        return trainingMapper.toDto(trainingService.updateTraining(updatedTraining));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTraining(@PathVariable Long id) {
        trainingService.deleteTraining(id);
    }
}
