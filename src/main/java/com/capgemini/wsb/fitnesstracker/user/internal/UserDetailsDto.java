package com.capgemini.wsb.fitnesstracker.user.internal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDetailsDto {
    // Getters and Setters
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    // Constructor
    public UserDetailsDto() {
    }

}
