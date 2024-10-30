package com.capgemini.wsb.fitnesstracker.user.api;

/**
 * Interface (API) for modifying operations on {@link User} entities through the API.
 * Implementing classes are responsible for executing changes within a database transaction, whether by continuing an existing transaction or creating a new one if required.
 */
public interface UserService {

    User createUser(User user);

    /**
     * Deletes a user based on their ID.
     *
     * @param userId id of the user to be deleted
     * @throws UserNotFoundException if user is not found
     */
    void deleteUser(Long userId);

    User updateUser(User user);
}
