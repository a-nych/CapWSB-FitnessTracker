package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.User;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

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
/* // This solution to user update is using reflection to avoid exposing User entity fields via Setters - Adam S.
    public User toUpdatedEntity(User user, UserUpdateDto updateDto) {
        Field[] fields = UserUpdateDto.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(updateDto);
                if (value != null) {
                    Field userField = User.class.getDeclaredField(field.getName());
                    userField.setAccessible(true);
                    userField.set(user, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to update field: " + field.getName(), e);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Field not found in User class: " + field.getName(), e);
            } finally {
                field.setAccessible(false);
            }
        }
        return user;
    }
*/

    public User toUpdatedEntity(User user, UserUpdateDto updateDto) {
    User updatedUser = new User(
            updateDto.firstName() != null ? updateDto.firstName() : user.getFirstName(),
            updateDto.lastName() != null ? updateDto.lastName() : user.getLastName(),
            updateDto.birthdate() != null ? updateDto.birthdate() : user.getBirthdate(),
            updateDto.email() != null ? updateDto.email() : user.getEmail()
    );
    updatedUser.setId(user.getId());
    return updatedUser;
}

}
