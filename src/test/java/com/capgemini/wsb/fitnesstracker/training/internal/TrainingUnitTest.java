package com.capgemini.wsb.fitnesstracker.training.internal;

import com.capgemini.wsb.fitnesstracker.training.api.InvalidActivityTypeException;
import com.capgemini.wsb.fitnesstracker.training.api.Training;
import com.capgemini.wsb.fitnesstracker.training.api.TrainingNotFoundException;
import com.capgemini.wsb.fitnesstracker.user.api.User;
import com.capgemini.wsb.fitnesstracker.user.api.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import com.capgemini.wsb.fitnesstracker.user.api.UserProvider;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TrainingUnitTest {

    @Nested
    class ActivityTypeTest {

        @Test
        void shouldReturnCorrectDisplayName() {
            assertThat(ActivityType.RUNNING.getDisplayName()).isEqualTo("Running");
            assertThat(ActivityType.CYCLING.getDisplayName()).isEqualTo("Cycling");
            assertThat(ActivityType.WALKING.getDisplayName()).isEqualTo("Walking");
            assertThat(ActivityType.SWIMMING.getDisplayName()).isEqualTo("Swimming");
            assertThat(ActivityType.TENNIS.getDisplayName()).isEqualTo("Tenis");
        }
    }

    @Nested
    class TrainingMapperTest {

        private TrainingMapper trainingMapper;

        @Mock
        private UserProvider userProvider;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            trainingMapper = new TrainingMapper(userProvider);
        }

        @Test
        void shouldMapTrainingToDto() {
            Training training = new Training(
                    new User("John", "Doe", LocalDate.of(1990, 1, 1) , "john.doe@example.com"),
                    new Date(),
                    new Date(),
                    ActivityType.RUNNING,
                    10.0,
                    5.0
            );

            TrainingDto dto = trainingMapper.toDto(training);

            assertThat(dto.getId()).isEqualTo(training.getId());
            assertThat(dto.getActivityType()).isEqualTo(training.getActivityType());
            assertThat(dto.getDistance()).isEqualTo(training.getDistance());
            assertThat(dto.getAverageSpeed()).isEqualTo(training.getAverageSpeed());
            assertThat(dto.getUser()).isEqualTo(training.getUser());
            assertThat(dto.getStartTime()).isEqualTo(training.getStartTime());
            assertThat(dto.getEndTime()).isEqualTo(training.getEndTime());
        }

        @Test
        void shouldMapCreateDtoToTraining() {
            TrainingCreateDto dto = new TrainingCreateDto();
            dto.setUserId(1L);
            dto.setActivityType(ActivityType.CYCLING);
            dto.setDistance(15.0);
            dto.setAverageSpeed(7.5);

            User user = new User("John", "Doe", LocalDate.of(1990, 1, 1) , "john.doe@example.com");
            when(userProvider.getUser(1L)).thenReturn(Optional.of(user));

            Training training = trainingMapper.toEntity(dto);

            assertThat(training.getActivityType()).isEqualTo(ActivityType.CYCLING);
            assertThat(training.getDistance()).isEqualTo(dto.getDistance());
            assertThat(training.getUser()).isEqualTo(user);
        }

        @Test
        void shouldUpdateEntityWithNewValues() {

            User existingUser = new User("John", "Doe", LocalDate.of(1990, 1, 1) , "john.doe@example.com");
            User updatedUser = new User("Jane", "Doe", LocalDate.of(1990, 1, 1) , "john.doe@example.com");

            Training existingTraining = new Training(
                    existingUser,
                    new Date(1000000),
                    new Date(2000000),
                    ActivityType.RUNNING,
                    5.0,
                    10.0
            );

            TrainingUpdateDto updateDto = new TrainingUpdateDto();
            updateDto.setUserId(2L);
            updateDto.setStartTime(new Date(3000000));
            updateDto.setEndTime(new Date(4000000));
            updateDto.setActivityType(ActivityType.CYCLING);
            updateDto.setDistance(15.0);
            updateDto.setAverageSpeed(20.0);

            when(userProvider.getUser(2L)).thenReturn(Optional.of(updatedUser));


            Training updatedTraining = trainingMapper.toUpdatedEntity(existingTraining, updateDto);


            assertThat(updatedTraining.getUser()).isEqualTo(updatedUser);
            assertThat(updatedTraining.getStartTime()).isEqualTo(updateDto.getStartTime());
            assertThat(updatedTraining.getEndTime()).isEqualTo(updateDto.getEndTime());
            assertThat(updatedTraining.getActivityType()).isEqualTo(updateDto.getActivityType());
            assertThat(updatedTraining.getDistance()).isEqualTo(updateDto.getDistance());
            assertThat(updatedTraining.getAverageSpeed()).isEqualTo(updateDto.getAverageSpeed());
        }

        @Test
        void shouldRetainExistingValuesIfUpdateDtoIsNull() {

            User existingUser = new User("John", "Doe", LocalDate.of(1990, 1, 1) , "john.doe@example.com");

            Training existingTraining = new Training(
                    existingUser,
                    new Date(1000000),
                    new Date(2000000),
                    ActivityType.RUNNING,
                    5.0,
                    10.0
            );

            TrainingUpdateDto updateDto = new TrainingUpdateDto();


            Training updatedTraining = trainingMapper.toUpdatedEntity(existingTraining, updateDto);


            assertThat(updatedTraining.getUser()).isEqualTo(existingUser);
            assertThat(updatedTraining.getStartTime()).isEqualTo(existingTraining.getStartTime());
            assertThat(updatedTraining.getEndTime()).isEqualTo(existingTraining.getEndTime());
            assertThat(updatedTraining.getActivityType()).isEqualTo(existingTraining.getActivityType());
            assertThat(updatedTraining.getDistance()).isEqualTo(existingTraining.getDistance());
            assertThat(updatedTraining.getAverageSpeed()).isEqualTo(existingTraining.getAverageSpeed());
        }

        @Test
        void shouldThrowExceptionWhenUserIdIsInvalid() {

            Training existingTraining = new Training(
                    new User("John", "Doe", LocalDate.of(1990, 1, 1) , "john.doe@example.com"),
                    new Date(),
                    new Date(),
                    ActivityType.RUNNING,
                    5.0,
                    10.0
            );

            TrainingUpdateDto updateDto = new TrainingUpdateDto();
            updateDto.setUserId(99L);

            when(userProvider.getUser(99L)).thenReturn(Optional.empty());


            assertThatThrownBy(() -> trainingMapper.toUpdatedEntity(existingTraining, updateDto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User not found with id: 99");
        }


    }

    @Nested
    class TrainingServiceImplTest {

        private TrainingServiceImpl trainingService;

        @Mock
        private TrainingRepository trainingRepository;

        @Mock
        private UserProvider userProvider;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            trainingService = new TrainingServiceImpl(trainingRepository, userProvider);
        }

        @Test
        void shouldFindAllTrainings() {
            List<Training> trainings = new ArrayList<>();
            trainings.add(new Training(
                    new User("John", "Doe", LocalDate.of(1990, 1, 1) , "john.doe@example.com"),
                    new Date(),
                    new Date(),
                    ActivityType.RUNNING,
                    10.0,
                    5.0
            ));
            when(trainingRepository.findAll()).thenReturn(trainings);

            List<Training> result = trainingService.findAllTrainings();

            assertThat(result).hasSize(1);
            verify(trainingRepository, times(1)).findAll();
        }

        @Test
        void shouldThrowExceptionWhenUserNotFoundForTrainingsByUserId() {
            when(userProvider.getUser(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> trainingService.findTrainingsByUserId(1L))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        void shouldReturnTrainingWhenFound() {

            Long trainingId = 1L;
            User user = new User("John", "Doe", LocalDate.of(1990, 1, 1) , "john.doe@example.com");
            Training training = new Training(user, null, null, null, 0.0, 0.0);
            training.setId(trainingId);

            when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));

            Optional<Training> result = trainingService.getTraining(trainingId);

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(training);
            verify(trainingRepository, times(1)).findById(trainingId);
        }

        @Test
        void shouldReturnEmptyWhenTrainingNotFound() {
            Long trainingId = 1L;

            when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());

            Optional<Training> result = trainingService.getTraining(trainingId);

            assertThat(result).isEmpty();
            verify(trainingRepository, times(1)).findById(trainingId);
        }
        @Test
        void shouldFindTrainingsByUserId() {
            Long userId = 1L;
            User user = new User("John", "Doe", LocalDate.of(1990, 1, 1) , "john.doe@example.com");
            Training training = new Training(user, null, null, null, 0.0, 0.0);

            when(userProvider.getUser(userId)).thenReturn(Optional.of(user));
            when(trainingRepository.findByUserId(userId)).thenReturn(List.of(training));

            List<Training> result = trainingService.findTrainingsByUserId(userId);

            assertThat(result).containsExactly(training);
            verify(userProvider, times(1)).getUser(userId);
            verify(trainingRepository, times(1)).findByUserId(userId);
        }

        @Test
        void shouldThrowExceptionWhenUserNotFoundInFindTrainingsByUserId() {
            Long userId = 1L;
            when(userProvider.getUser(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> trainingService.findTrainingsByUserId(userId))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining(userId.toString());
            verify(userProvider, times(1)).getUser(userId);
            verify(trainingRepository, never()).findByUserId(any());
        }

        @Test
        void shouldFindTrainingsFinishedAfter() {
            Date date = new Date();
            Training training = new Training(null, null, date, null, 0.0, 0.0);

            when(trainingRepository.findByEndTimeAfter(date)).thenReturn(List.of(training));

            List<Training> result = trainingService.findTrainingsFinishedAfter(date);

            assertThat(result).containsExactly(training);
            verify(trainingRepository, times(1)).findByEndTimeAfter(date);
        }

        @Test
        void shouldFindTrainingsByActivityType() {
            String activityType = "RUNNING";
            ActivityType type = ActivityType.RUNNING;
            Training training = new Training(null, null, null, type, 0.0, 0.0);

            when(trainingRepository.findByActivityType(type)).thenReturn(List.of(training));

            List<Training> result = trainingService.findTrainingsByActivityType(activityType);

            assertThat(result).containsExactly(training);
            verify(trainingRepository, times(1)).findByActivityType(type);
        }

        @Test
        void shouldThrowExceptionForInvalidActivityType() {
            String invalidActivityType = "INVALID_TYPE";

            assertThatThrownBy(() -> trainingService.findTrainingsByActivityType(invalidActivityType))
                    .isInstanceOf(InvalidActivityTypeException.class)
                    .hasMessageContaining(invalidActivityType);
            verify(trainingRepository, never()).findByActivityType(any());
        }

        @Test
        void shouldCreateTraining() {
            Training training = new Training(null, null, null, null, 0.0, 0.0);

            when(trainingRepository.save(training)).thenReturn(training);

            Training result = trainingService.createTraining(training);

            assertThat(result).isEqualTo(training);
            verify(trainingRepository, times(1)).save(training);
        }

        @Test
        void shouldThrowExceptionWhenCreateTrainingWithId() {
            Training training = new Training(null, null, null, null, 0.0, 0.0);
            training.setId(1L);

            assertThatThrownBy(() -> trainingService.createTraining(training))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Training has already DB ID");
            verify(trainingRepository, never()).save(training);
        }

        @Test
        void shouldUpdateTraining() {
            Long trainingId = 1L;
            Training training = new Training(null, null, null, null, 0.0, 0.0);
            training.setId(trainingId);

            when(trainingRepository.existsById(trainingId)).thenReturn(true);
            when(trainingRepository.save(training)).thenReturn(training);

            Training result = trainingService.updateTraining(training);

            assertThat(result).isEqualTo(training);
            verify(trainingRepository, times(1)).existsById(trainingId);
            verify(trainingRepository, times(1)).save(training);
        }

        @Test
        void shouldThrowExceptionWhenUpdateTrainingWithoutId() {
            Training training = new Training(null, null, null, null, 0.0, 0.0);

            assertThatThrownBy(() -> trainingService.updateTraining(training))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Training has no ID");
            verify(trainingRepository, never()).existsById(any());
            verify(trainingRepository, never()).save(training);
        }

        @Test
        void shouldThrowExceptionWhenUpdateTrainingWithNonexistentId() {
            Long trainingId = 1L;
            Training training = new Training(null, null, null, null, 0.0, 0.0);
            training.setId(trainingId);

            when(trainingRepository.existsById(trainingId)).thenReturn(false);

            assertThatThrownBy(() -> trainingService.updateTraining(training))
                    .isInstanceOf(TrainingNotFoundException.class)
                    .hasMessageContaining(trainingId.toString());
            verify(trainingRepository, times(1)).existsById(trainingId);
            verify(trainingRepository, never()).save(training);
        }

        @Test
        void shouldDeleteTraining() {
            Long trainingId = 1L;
            Training training = new Training(null, null, null, null, 0.0, 0.0);
            training.setId(trainingId);

            when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));

            trainingService.deleteTraining(trainingId);

            verify(trainingRepository, times(1)).findById(trainingId);
            verify(trainingRepository, times(1)).delete(training);
        }

        @Test
        void shouldThrowExceptionWhenDeleteTrainingWithNonexistentId() {
            Long trainingId = 1L;

            when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> trainingService.deleteTraining(trainingId))
                    .isInstanceOf(TrainingNotFoundException.class)
                    .hasMessageContaining(trainingId.toString());
            verify(trainingRepository, times(1)).findById(trainingId);
            verify(trainingRepository, never()).delete(any());
        }
    }

    @Nested
    class TrainingControllerTest {

        private MockMvc mockMvc;

        @Mock
        private TrainingServiceImpl trainingService;

        @Mock
        private TrainingMapper trainingMapper;

        private TrainingController trainingController;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            trainingController = new TrainingController(trainingService, trainingMapper);
            mockMvc = MockMvcBuilders.standaloneSetup(trainingController).build();
        }

        @Test
        void shouldReturnAllTrainings() throws Exception {
            when(trainingService.findAllTrainings()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/v1/trainings")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        void shouldReturnTrainingsForUser() throws Exception {
            Long userId = 1L;
            User User = new User("John", "Doe", LocalDate.of(1990, 1, 1) , "john.doe@example.com");
            Training training = new Training(
                    User,
                    new Date(1000000),
                    new Date(2000000),
                    ActivityType.RUNNING,
                    5.0,
                    10.0
            );
            TrainingDto trainingDto = new TrainingDto();

            when(trainingService.findTrainingsByUserId(userId)).thenReturn(List.of(training));
            when(trainingMapper.toDto(training)).thenReturn(trainingDto);

            mockMvc.perform(get("/v1/trainings/" + userId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0]").exists());

            verify(trainingService, times(1)).findTrainingsByUserId(userId);
            verify(trainingMapper, times(1)).toDto(training);
        }

        @Test
        void shouldReturnTrainingsFinishedAfter() throws Exception {
            String dateStr = "2024-01-01";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(dateStr);
            User User = new User("John", "Doe", LocalDate.of(1990, 1, 1) , "john.doe@example.com");
            Training training = new Training(
                    User,
                    new Date(1000000),
                    new Date(2000000),
                    ActivityType.RUNNING,
                    5.0,
                    10.0
            );
            TrainingDto trainingDto = new TrainingDto();

            when(trainingService.findTrainingsFinishedAfter(date)).thenReturn(List.of(training));
            when(trainingMapper.toDto(training)).thenReturn(trainingDto);

            mockMvc.perform(get("/v1/trainings/finished/" + dateStr))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0]").exists());

            verify(trainingService, times(1)).findTrainingsFinishedAfter(date);
            verify(trainingMapper, times(1)).toDto(training);
        }

        @Test
        void shouldReturnBadRequestForInvalidDate() throws Exception {
            String invalidDate = "invalid-date";

            mockMvc.perform(get("/v1/trainings/finished/" + invalidDate))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnTrainingsByActivityType() throws Exception {
            String activityType = "RUNNING";
            User User = new User("John", "Doe", LocalDate.of(1990, 1, 1) , "john.doe@example.com");
            Training training = new Training(
                    User,
                    new Date(1000000),
                    new Date(2000000),
                    ActivityType.RUNNING,
                    5.0,
                    10.0
            );
            TrainingDto trainingDto = new TrainingDto();

            when(trainingService.findTrainingsByActivityType(activityType)).thenReturn(List.of(training));
            when(trainingMapper.toDto(training)).thenReturn(trainingDto);

            mockMvc.perform(get("/v1/trainings/activityType").param("activityType", activityType))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[0]").exists());

            verify(trainingService, times(1)).findTrainingsByActivityType(activityType);
            verify(trainingMapper, times(1)).toDto(training);
        }

        @Test
        void shouldReturnBadRequestForMissingActivityType() throws Exception {
            mockMvc.perform(get("/v1/trainings/activityType"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldDeleteTraining() throws Exception {
            Long trainingId = 1L;

            mockMvc.perform(delete("/v1/trainings/" + trainingId))
                    .andExpect(status().isNoContent());

            verify(trainingService, times(1)).deleteTraining(trainingId);
        }

        @Test
        void shouldReturnNotFoundForDeletingNonexistentTraining() throws Exception {
            Long trainingId = 1L;

            doThrow(new TrainingNotFoundException(trainingId)).when(trainingService).deleteTraining(trainingId);

            mockMvc.perform(delete("/v1/trainings/" + trainingId))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldCreateTraining() throws Exception {
            TrainingCreateDto trainingCreateDto = new TrainingCreateDto();
            trainingCreateDto.setUserId(1L);
            trainingCreateDto.setActivityType(ActivityType.RUNNING);
            trainingCreateDto.setDistance(10.0);
            trainingCreateDto.setAverageSpeed(5.0);
            User user = new User("John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com");
            Training training = new Training(user, new Date(1000000), new Date(2000000), ActivityType.RUNNING, 10.0, 5.0);
            TrainingDto trainingDto = new TrainingDto();

            doReturn(training).when(trainingMapper).toEntity(any(TrainingCreateDto.class));
            when(trainingService.createTraining(training)).thenReturn(training);
            when(trainingMapper.toDto(training)).thenReturn(trainingDto);
            mockMvc.perform(post("/v1/trainings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"userId\": 1, \"activityType\": \"RUNNING\", \"distance\": 10.0, \"averageSpeed\": 5.0}"))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").exists());
            verify(trainingService, times(1)).createTraining(training);
            verify(trainingMapper, times(1)).toDto(training);
        }

        @Test
        void shouldUpdateTraining() throws Exception {
            TrainingUpdateDto trainingUpdateDto = new TrainingUpdateDto();
            trainingUpdateDto.setUserId(1L);
            trainingUpdateDto.setActivityType(ActivityType.CYCLING);
            trainingUpdateDto.setDistance(15.0);
            trainingUpdateDto.setAverageSpeed(12.0);
            trainingUpdateDto.setStartTime(new Date(1000000));
            trainingUpdateDto.setEndTime(new Date(2000000));

            User user = new User("John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com");
            user.setId(1L);
            Training existingTraining = new Training(user, new Date(1000000), new Date(2000000), ActivityType.RUNNING, 10.0, 5.0);
            Training updatedTraining = new Training(user, new Date(1000000), new Date(2000000), ActivityType.CYCLING, 15.0, 12.0);
            TrainingDto updatedTrainingDto = new TrainingDto();

            existingTraining.setId(1L);
            updatedTraining.setId(1L);

            when(trainingService.getTraining(1L)).thenReturn(Optional.of(existingTraining));
            doReturn(updatedTraining).when(trainingMapper).toUpdatedEntity(any(Training.class), any(TrainingUpdateDto.class));
            when(trainingService.updateTraining(updatedTraining)).thenReturn(updatedTraining);
            when(trainingMapper.toDto(updatedTraining)).thenReturn(updatedTrainingDto);

            mockMvc.perform(put("/v1/trainings/" + existingTraining.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"userId\": 1, \"activityType\": \"CYCLING\", \"distance\": 15.0, \"averageSpeed\": 12.0, \"startTime\": \"2024-11-22T01:00:00Z\", \"endTime\": \"2024-11-22T02:00:00Z\"}"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").exists());

            verify(trainingService, times(1)).getTraining(1L);
            verify(trainingService, times(1)).updateTraining(updatedTraining);
            verify(trainingMapper, times(1)).toDto(updatedTraining);
        }

    }
}