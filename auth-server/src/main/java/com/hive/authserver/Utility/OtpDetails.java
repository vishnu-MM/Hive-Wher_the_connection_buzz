package com.hive.authserver.Utility;

public record OtpDetails(String username, String otp, Long timeOfCreation) {}