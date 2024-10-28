package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.user.api.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.List;
import java.util.Optional;

interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Query searching users by email address. It matches by exact match.
     *
     * @param email email of the user to search
     * @return {@link Optional} containing found user or {@link Optional#empty()} if none matched
     */
    default Optional<User> findByEmail(String email) {
        return findAll().stream()
                        .filter(user -> Objects.equals(user.getEmail(), email))
                        .findFirst();
    }

    /**
     * Query users by email address. It matches by partial match.
     *
     * @param email partial email of the user to search
     * @return {@link List} of matching users by email
     */
    default List<User> searchByPartialEmail(String email) {
        return findAll().stream()
                .filter(user -> user.getEmail().toLowerCase().contains(email.toLowerCase()))
                .toList();
    }

    /**
     * Query users by their age. Returns people older than specified date.
     *
     * @param time Minimum date to search from
     * @return {@link List} of matching users
     */
    default List<User> searchUsersOlderThan(LocalDate time) {
        if (time == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        return findAll().stream()
                .filter(user -> user.getBirthdate().isBefore(time))
                .toList();
    }
}
