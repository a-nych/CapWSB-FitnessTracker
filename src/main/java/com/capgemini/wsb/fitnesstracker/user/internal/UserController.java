package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.User;
import com.capgemini.wsb.fitnesstracker.user.api.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("/search")
    public List<UserIdDto> searchUser(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer age) {

        if ((email == null && age == null) || (email != null && age != null)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Exactly one search parameter (email or age) must be provided"
            );
        }

        if (email != null) {
            return userService.searchUsersByPartialEmail(email)
                    .stream()
                    .map(userMapper::toIdsDto)
                    .toList();
        } else {
            return userService.searchUsersByAge(age)
                    .stream()
                    .map(userMapper::toIdsDto)
                    .toList();
        }
    }

    @PostMapping
    public User addUser(@RequestBody UserDto userDto) throws InterruptedException {
        User newUser = userMapper.toEntity(userDto);

        userService.createUser(newUser);

        return newUser;
    }

    @PatchMapping("/{id}")
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
