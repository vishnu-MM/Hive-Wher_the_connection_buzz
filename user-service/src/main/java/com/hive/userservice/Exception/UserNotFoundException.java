package com.hive.userservice.Exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String username) {
        super("User not found: " + username);
    }
}