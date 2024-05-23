package com.hive.authserver.Service;

import com.hive.authserver.DTO.UserDTO;
import com.hive.authserver.Utility.OtpDetails;
import com.hive.authserver.Utility.OtpVerificationStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final JavaMailSender javaMailSender;
    private final Logger log = LoggerFactory.getLogger(OtpService.class);
    private static final long OTP_VALIDITY_DURATION = 5* 60* 1000;
    private final Map<String, OtpDetails> availableOtps = new HashMap<>();
    @Value("${spring.mail.properties.mail.username}") private String from;
    


    public void sendOTP(UserDTO recipient) {
        String recipientEmail = recipient.getEmail();
        String recipientUsername = recipient.getUsername();
        String otp = generateOTP();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipientEmail);
        message.setSubject("OTP Verification - HIVE");
        message.setText(generateEmailBody(recipientUsername, otp));

        try {
            javaMailSender.send(message);
            System.out.println("OTP "+otp);
            availableOtps.put(recipientEmail, new OtpDetails(recipientEmail, otp, System.currentTimeMillis()));
            log.info("OTP sent successfully {}", availableOtps.get(recipientEmail));
        }
        catch (Exception e) {
            log.error("Error Occurred while sending OTP verification");
            throw new RuntimeException("Error Occurred while sending OTP verification");
        }
    }

    public OtpVerificationStatus verifyOTP(String recipient, String enteredOtp) {

        if (recipient == null || recipient.isEmpty())
            return OtpVerificationStatus.INVALID_USER;
        if (!availableOtps.containsKey(recipient))
            return OtpVerificationStatus.INVALID_USER;

        OtpDetails otpDetails = availableOtps.get(recipient);

        if (enteredOtp == null || enteredOtp.length() != 6)
            return OtpVerificationStatus.INVALID_OTP;
        if(!enteredOtp.equals(otpDetails.otp()))
            return OtpVerificationStatus.INVALID_OTP;

        if (System.currentTimeMillis() - otpDetails.timeOfCreation() > OTP_VALIDITY_DURATION)
            return OtpVerificationStatus.TIME_OUT;

        return  OtpVerificationStatus.SUCCESS;
    }

    public String generateOTP() {
        Random random = new Random();
        int min = 111111;
        int max = 999999;
        int OneTimePassword = random.nextInt(max - min + 1) + min;
        return Integer.toString(OneTimePassword);
    }

    private String generateEmailBody(String name, String otp ) {
        return "Hello " + name + ",\n\nYour One-Time Password (OTP) is: " + otp +
                "\n\nThis OTP is valid for a short period. Please use it for authentication as soon as possible.\n\n" +
                "If you didn't request this OTP, please contact our support team.\n\nEnjoy shopping.";
    }
}
