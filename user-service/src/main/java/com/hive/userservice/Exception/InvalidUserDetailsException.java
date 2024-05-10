package com.hive.userservice.Exception;

public class InvalidUserDetailsException extends Throwable {
    public InvalidUserDetailsException(String username) {
        super("Invalid username Details" + username);
    }
}
