package com.capgemini.wsb.fitnesstracker.user.internal;

import jakarta.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;


record UserUpdateDto(
        @Nullable String firstName,
        @Nullable String lastName,
        @Nullable @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthdate,
        @Nullable String email
) {}