package com.hive.authserver.CustomException;

public class UserExistsException extends Exception {
    public UserExistsException(String message) {
        super(message);
    }
}
