package com.OTPvalidation.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String email, String subject, String motp, String eotp) {
        // Logic to send email using an email service provider (e.g., SMTP, SendGrid, etc.)
        // This is a placeholder implementation; you would replace it with actual email-sending logic.
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText("Your OTP is: " + motp + "\n" +
                "Your email OTP is: " + eotp + "\n" +
                "Please use this OTP to verify your account.");
        mailSender.send(message);
    }
}
