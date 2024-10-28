package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.User;
import com.capgemini.wsb.fitnesstracker.user.api.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserServiceImpl userService;

    private final UserMapper userMapper;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @GetMapping("/simple")
    public List<UserSimpleDto> getAllSimpleUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toSimpleDto)
                .toList();
    }

    @GetMapping("/{id}")
    public Optional<UserDto> getUser(@PathVariable Long id) {
        return userService.getUser(id)
                .map(userMapper::toDto);
    }
    @GetMapping("/email")
    public List<UserIdDto> searchUserByEmail(@RequestParam String email) {
        if (email == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Parameter (email) must be provided"
            );
        }
        return userService.searchUsersByPartialEmail(email)
                .stream()
                .map(userMapper::toIdsDto)
                .toList();
    }

    @GetMapping("/older/{time}")
    public List<UserDto> searchUserOlderThan(@PathVariable LocalDate time) {

        if (time == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Parameter (date yyyy-mm-dd) must be provided"
            );
        }
            return userService.searchUsersOlderThan(time)
                    .stream()
                    .map(userMapper::toDto)
                    .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@RequestBody UserDto userDto) {
        User newUser = userMapper.toEntity(userDto);
        userService.createUser(newUser);
        return newUser;
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserUpdateDto updateDto) {
        User user = userService.getUser(id).orElseThrow(() -> new UserNotFoundException(id));
        User updatedUser = userMapper.toUpdatedEntity(user, updateDto);

        return userMapper.toDto(userService.updateUser(updatedUser));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
