package com.OTPvalidation.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void saveOtp(String key, String value,long expirationTime) {
        redisTemplate.opsForValue().set(key, value, expirationTime); // Set expiration time to 1 hour (3600 seconds)
    }

    public String getOtp(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteOtp(String key) {
        redisTemplate.delete(key);
    }

    public String gnerateOtp() {
        // Generate a random 6-digit OTP
        int otp = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(otp);
    }

    public boolean validateOtp(String key, String otp) {
        String storedOtp = getOtp(key);
        if (storedOtp != null && storedOtp.equals(otp)) {
            deleteOtp(key); // Delete OTP after successful validation
            return true;
        }
        return false;
    }
}
