package com.OTPvalidation.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.OTPvalidation.Entity.Otp;
import com.OTPvalidation.Entity.User;
import com.OTPvalidation.Service.EmailService;
import com.OTPvalidation.Service.OtpService;
import com.OTPvalidation.Service.UserSeriviceImpl;

import jakarta.validation.Valid;

@RestController
public class UserController {

    // Assuming you have a UserService to handle user-related operations
    @Autowired
    private UserSeriviceImpl userSerivice;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService; // Assuming you have an email service to send OTPs

    @PostMapping("/add")
    public ResponseEntity<String> addUser(@Valid @RequestBody User user) {
        // Call the service method to add the user
        String tempMessage = userSerivice.addUser(user);

        // Generate a unique session ID
        String sessionId = java.util.UUID.randomUUID().toString();

        // Generate OTPs for email and phone number
        String eotp = otpService.gnerateOtp();
        String motp = otpService.gnerateOtp();

        // Save session ID mapping in Redis
        otpService.saveOtp("session:" + sessionId + ":email", user.getEmail(), 3600); // Map session ID to email
        otpService.saveOtp("session:" + sessionId + ":phone", user.getPhoneNumber(), 3600); // Map session ID to phone

        // Save OTPs in Redis with session ID
        otpService.saveOtp("otp:session:" + sessionId + ":email", eotp, 3600); // Save email OTP
        otpService.saveOtp("otp:session:" + sessionId + ":phone", motp, 3600); // Save phone OTP

        // Send OTPs to the user via email
        emailService.sendEmail(user.getEmail(), "OTP Verification for Application",eotp, motp);

        // Create a cookie for the session ID
        ResponseCookie sessionCookie = ResponseCookie.from("sessionId", sessionId)
                .httpOnly(true) // Prevent JavaScript access to the cookie
                .secure(true) // Use HTTPS in production
                .path("/") // Cookie is available for all paths
                .maxAge(3600) // Expiration time in seconds
                .build();

        // Return the response with the cookie
        return ResponseEntity.ok()
                .header("Set-Cookie", sessionCookie.toString())
                .body(tempMessage);
    }

    @PostMapping("/verify")
    public String verifyOtp(@CookieValue("sessionId") String sessionId, @RequestBody Otp otp) {
        // Retrieve email and phone number from Redis using session ID
        String email = otpService.getOtp("session:" + sessionId + ":email"); // Correct Redis key for email
        String phoneNumber = otpService.getOtp("session:" + sessionId + ":phone"); // Correct Redis key for phone number

        if (email == null || phoneNumber == null) {
            return "Invalid session ID!";
        }

        // Retrieve OTPs from Redis using session ID
        String storedEmailOtp = otpService.getOtp("otp:session:" + sessionId + ":email"); // Correct Redis key for email OTP
        String storedPhoneOtp = otpService.getOtp("otp:session:" + sessionId + ":phone"); // Correct Redis key for phone OTP

        System.out.println("Session ID: " + sessionId);
        System.out.println(storedEmailOtp + " " + storedPhoneOtp);
        System.out.println(otp.getEotp() + " " + otp.getMotp());
        // Validate the OTPs
        if (storedEmailOtp != null && storedEmailOtp.trim().equals(otp.getEotp().trim()) &&
            storedPhoneOtp != null && storedPhoneOtp.trim().equals(otp.getMotp().trim())) {
            
            // OTPs are valid, mark the user as verified
            User user = userSerivice.getUserByEmail(email);
            if (user != null) {
                user.setVerified(true);
                userSerivice.updateUser(user);
                return "OTP verified successfully! User is now verified.";
            } else {
                return "User not found!";
            }
        } else {
            return "Invalid or expired OTP!";
        }
    }

    @PatchMapping("/update")
    public String updateUser() {
        return "User updated successfully!";
    }

    @GetMapping("/get")
    public List<User> getUser() {
        return userSerivice.getUser();
    }

    @GetMapping("/get/{id}")
    public String getUserById(@PathVariable Long id) {
        return "User retrieved successfully by ID!";
    }


}
