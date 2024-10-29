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

    public User toUpdatedEntity(User user, UserUpdateDto updateDto) {
        // Using reflection to avoid exposing User entity fields via Setters, if better performance is a priority then setters in User class should be made and this solution changed to the one below - Adam S.
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

    /*
    public User toUpdatedEntity(User user, UserUpdateDto updateDto) {
    User updatedUser = new User(
            updateDto.firstName() != null ? updateDto.firstName() : existingUser.getFirstName(),
            updateDto.lastName() != null ? updateDto.lastName() : existingUser.getLastName(),
            updateDto.birthdate() != null ? updateDto.birthdate() : existingUser.getBirthdate(),
            updateDto.email() != null ? updateDto.email() : existingUser.getEmail()
    );
    updatedUser.setId(existingUser.getId()); // only possible by adding @setter annotation to User class
    return updatedUser;
}
     */
}
