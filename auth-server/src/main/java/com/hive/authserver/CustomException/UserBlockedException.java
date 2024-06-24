package com.hive.authserver.CustomException;

public class UserBlockedException extends Exception {
    public UserBlockedException(String message) {
        super(message);
    }
}
