package com.example.usermanagement.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("User not found.");
    }

}
