package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.User;
import org.springframework.stereotype.Component;

@Component
class UserMapper {

    UserDto toDto(User user) {
        return new UserDto(user.getId(),
                           user.getFirstName(),
                           user.getLastName(),
                           user.getBirthdate(),
                           user.getEmail());
    }

    UserSimpleDto toSimpleDto(User user) {
        return new UserSimpleDto(user.getId(),
                user.getFirstName(),
                user.getLastName());
    }

    User toEntity(UserDto userDto) {
        return new User(
                        userDto.firstName(),
                        userDto.lastName(),
                        userDto.birthdate(),
                        userDto.email());
    }

    UserIdDto toIdsDto(User user) {
        return new UserIdDto(
                user.getId(),
                user.getEmail()
        );
    }

    User toUpdatedEntity(User existingUser, UserUpdateDto updateDto) {
        User updatedUser = new User(
                updateDto.firstName() != null ? updateDto.firstName() : existingUser.getFirstName(),
                updateDto.lastName() != null ? updateDto.lastName() : existingUser.getLastName(),
                updateDto.birthdate() != null ? updateDto.birthdate() : existingUser.getBirthdate(),
                updateDto.email() != null ? updateDto.email() : existingUser.getEmail()
        );
        // updatedUser.setId(existingUser.getId()); // TODO: fix me
        return updatedUser;
    }

}
