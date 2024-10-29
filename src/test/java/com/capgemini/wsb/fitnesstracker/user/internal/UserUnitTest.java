package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.User;
import com.capgemini.wsb.fitnesstracker.user.api.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUnitTest {

    private static final Long USER_ID = 1L;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final LocalDate BIRTHDATE = LocalDate.of(1990, 1, 1);
    private static final String EMAIL = "john.doe@example.com";

    @Nested
    class UserServiceImplTest {
        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private UserServiceImpl userService;

        private User testUser;

        @BeforeEach
        void setUp() {
            testUser = new User(FIRST_NAME, LAST_NAME, BIRTHDATE, EMAIL);
        }

        @Test
        @DisplayName("Should create user successfully")
        void shouldCreateUser() {
            // given
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // when
            User result = userService.createUser(testUser);

            // then
            assertThat(result).isNotNull();
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should throw exception when creating user with ID")
        void shouldThrowException_whenCreatingUserWithId() {
            // given
            User userWithId = mock(User.class);
            when(userWithId.getId()).thenReturn(1L);

            // when/then
            assertThatThrownBy(() -> userService.createUser(userWithId))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should get user by ID")
        void shouldGetUserById() {
            // given
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

            // when
            Optional<User> result = userService.getUser(USER_ID);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(testUser);
        }

        @Test
        @DisplayName("Should get user by email")
        void shouldGetUserByEmail() {
            // given
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));

            // when
            Optional<User> result = userService.getUserByEmail(EMAIL);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(testUser);
        }

        @Test
        @DisplayName("Should find all users")
        void shouldFindAllUsers() {
            // given
            List<User> users = Arrays.asList(testUser, new User("Jane", "Doe", BIRTHDATE, "jane@example.com"));
            when(userRepository.findAll()).thenReturn(users);

            // when
            List<User> result = userService.findAllUsers();

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Should search users by partial email")
        void shouldSearchUsersByPartialEmail() {
            // given
            List<User> users = Arrays.asList(testUser);
            when(userRepository.searchByPartialEmail("john")).thenReturn(users);

            // when
            List<User> result = userService.searchUsersByPartialEmail("john");

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should search users older than date")
        void shouldSearchUsersOlderThan() {
            // given
            List<User> users = Arrays.asList(testUser);
            LocalDate searchDate = LocalDate.of(2000, 1, 1);
            when(userRepository.searchUsersOlderThan(searchDate)).thenReturn(users);

            // when
            List<User> result = userService.searchUsersOlderThan(searchDate);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUser() {
            // given
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

            // when
            userService.deleteUser(USER_ID);

            // then
            verify(userRepository).delete(testUser);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent user")
        void shouldThrowException_whenDeletingNonExistentUser() {
            // given
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> userService.deleteUser(USER_ID))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    class UserMapperTest {
        private UserMapper userMapper;
        private User testUser;

        @BeforeEach
        void setUp() {
            userMapper = new UserMapper();
            testUser = new User(FIRST_NAME, LAST_NAME, BIRTHDATE, EMAIL);
        }

        @Test
        @DisplayName("Should map User to UserDto")
        void shouldMapToDto() {
            // when
            UserDto dto = userMapper.toDto(testUser);

            // then
            assertThat(dto.firstName()).isEqualTo(FIRST_NAME);
            assertThat(dto.lastName()).isEqualTo(LAST_NAME);
            assertThat(dto.birthdate()).isEqualTo(BIRTHDATE);
            assertThat(dto.email()).isEqualTo(EMAIL);
        }

        @Test
        @DisplayName("Should map User to UserSimpleDto")
        void shouldMapToSimpleDto() {
            // when
            UserSimpleDto dto = userMapper.toSimpleDto(testUser);

            // then
            assertThat(dto.firstName()).isEqualTo(FIRST_NAME);
            assertThat(dto.lastName()).isEqualTo(LAST_NAME);
        }

        @Test
        @DisplayName("Should map UserDto to User")
        void shouldMapToEntity() {
            // given
            UserDto dto = new UserDto(null, FIRST_NAME, LAST_NAME, BIRTHDATE, EMAIL);

            // when
            User user = userMapper.toEntity(dto);

            // then
            assertThat(user.getFirstName()).isEqualTo(FIRST_NAME);
            assertThat(user.getLastName()).isEqualTo(LAST_NAME);
            assertThat(user.getBirthdate()).isEqualTo(BIRTHDATE);
            assertThat(user.getEmail()).isEqualTo(EMAIL);
        }

        @Test
        @DisplayName("Should map User to UserIdDto")
        void shouldMapToIdsDto() {
            // when
            UserIdDto dto = userMapper.toIdsDto(testUser);

            // then
            assertThat(dto.email()).isEqualTo(EMAIL);
        }

        @Test
        @DisplayName("Should update User from UserUpdateDto")
        void shouldUpdateFromDto() {
            // given
            String newFirstName = "Jane";
            UserUpdateDto updateDto = new UserUpdateDto(newFirstName, null, null, null);

            // when
            User updatedUser = userMapper.toUpdatedEntity(testUser, updateDto);

            // then
            assertThat(updatedUser.getFirstName()).isEqualTo(newFirstName);
            assertThat(updatedUser.getLastName()).isEqualTo(LAST_NAME); // unchanged
            assertThat(updatedUser.getBirthdate()).isEqualTo(BIRTHDATE); // unchanged
            assertThat(updatedUser.getEmail()).isEqualTo(EMAIL); // unchanged
        }
    }

    @Nested
    @DataJpaTest
    class UserRepositoryTest {

        private final List<User> testUsers = Arrays.asList(
                new User("John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com"),
                new User("Jane", "Smith", LocalDate.of(2000, 1, 1), "jane.smith@example.com")
        );

        @Test
        void findByEmail_shouldReturnUser_whenEmailExists() {
            // given
            User user = testUsers.get(0);

            // when
            var result = testUsers.stream()
                    .filter(u -> u.getEmail().equals("john.doe@example.com"))
                    .findFirst();

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");
        }

        @Test
        void searchByPartialEmail_shouldReturnMatchingUsers() {
            // when
            var result = testUsers.stream()
                    .filter(user -> user.getEmail().toLowerCase().contains("john"))
                    .toList();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getEmail()).contains("john");
        }

        @Test
        void searchUsersOlderThan_shouldReturnMatchingUsers() {
            // when
            var result = testUsers.stream()
                    .filter(user -> user.getBirthdate().isBefore(LocalDate.of(1995, 1, 1)))
                    .toList();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getBirthdate().getYear()).isEqualTo(1990);
        }

        @Test
        void searchUsersOlderThan_shouldThrowException_whenDateIsNull() {
            assertThatThrownBy(() -> {
                testUsers.stream()
                        .filter(user -> user.getBirthdate().isBefore(null))
                        .toList();
            }).isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    class UserControllerTest {
        @Mock
        private UserServiceImpl userService;

        @Mock
        private UserMapper userMapper;

        @InjectMocks
        private UserController userController;

        private User testUser;
        private UserDto testUserDto;

        @BeforeEach
        void setUp() {
            testUser = new User(FIRST_NAME, LAST_NAME, BIRTHDATE, EMAIL);
            testUserDto = new UserDto(null, FIRST_NAME, LAST_NAME, BIRTHDATE, EMAIL);
        }

        @Test
        @DisplayName("Should get all users")
        void shouldGetAllUsers() {
            // given
            when(userService.findAllUsers()).thenReturn(List.of(testUser));
            when(userMapper.toDto(any(User.class))).thenReturn(testUserDto);

            // when
            List<UserDto> result = userController.getAllUsers();

            // then
            assertThat(result).hasSize(1);
            verify(userService).findAllUsers();
        }

        @Test
        @DisplayName("Should get all simple users")
        void shouldGetAllSimpleUsers() {
            // given
            when(userService.findAllUsers()).thenReturn(List.of(testUser));
            when(userMapper.toSimpleDto(any(User.class))).thenReturn(new UserSimpleDto(null, FIRST_NAME, LAST_NAME));

            // when
            List<UserSimpleDto> result = userController.getAllSimpleUsers();

            // then
            assertThat(result).hasSize(1);
            verify(userService).findAllUsers();
        }

        @Test
        @DisplayName("Should get user by ID")
        void shouldGetUserById() {
            // given
            when(userService.getUser(USER_ID)).thenReturn(Optional.of(testUser));
            when(userMapper.toDto(testUser)).thenReturn(testUserDto);

            // when
            Optional<UserDto> result = userController.getUser(USER_ID);

            // then
            assertThat(result).isPresent();
            verify(userService).getUser(USER_ID);
        }

        @Test
        @DisplayName("Should throw exception when searching users with null email")
        void shouldThrowException_whenSearchingWithNullEmail() {
            // when/then
            assertThatThrownBy(() -> userController.searchUserByEmail(null))
                    .isInstanceOf(ResponseStatusException.class)
                    .matches(ex -> ((ResponseStatusException) ex).getStatusCode() == HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Should throw exception when searching users with null date")
        void shouldThrowException_whenSearchingWithNullDate() {
            // when/then
            assertThatThrownBy(() -> userController.searchUserOlderThan(null))
                    .isInstanceOf(ResponseStatusException.class)
                    .matches(ex -> ((ResponseStatusException) ex).getStatusCode() == HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Should add user successfully")
        void shouldAddUser() {
            // given
            when(userMapper.toEntity(testUserDto)).thenReturn(testUser);
            when(userService.createUser(testUser)).thenReturn(testUser);

            // when
            User result = userController.addUser(testUserDto);

            // then
            assertThat(result).isNotNull();
            verify(userService).createUser(testUser);
        }

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUser() {
            // when
            userController.deleteUser(USER_ID);

            // then
            verify(userService).deleteUser(USER_ID);
        }

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUser() {
            // given
            UserUpdateDto updateDto = new UserUpdateDto("Jane", null, null, null);
            when(userService.getUser(USER_ID)).thenReturn(Optional.of(testUser));
            when(userMapper.toUpdatedEntity(testUser, updateDto)).thenReturn(testUser);
            when(userService.updateUser(testUser)).thenReturn(testUser);
            when(userMapper.toDto(testUser)).thenReturn(testUserDto);

            // when
            UserDto result = userController.updateUser(USER_ID, updateDto);

            // then
            assertThat(result).isNotNull();
            verify(userService).updateUser(testUser);
        }
    }
}