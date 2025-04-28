package com.OTPvalidation.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.OTPvalidation.Entity.User;
import com.OTPvalidation.Repository.UserRepository;

@Service
public class UserSeriviceImpl implements UserService {
    // Implement the methods from UserService interface here

    // Assuming you have a UserRepository to interact with the database
    @Autowired
    private UserRepository userRepository;

    @Override
    public String addUser(User user) {
        // Logic to add user to the database
        userRepository.save(user);
        return "Please verify your email and phone number for completing the application !";
    }

    @Override
    public List<User> getUser() {
        // TODO Auto-generated method stub
        return userRepository.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        // TODO Auto-generated method stub
        return userRepository.findByEmail(email);
    }

    @Override
    public String updateUser(User user) {
        // TODO Auto-generated method stub
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            existingUser.setName(user.getName());
            existingUser.setPhoneNumber(user.getPhoneNumber());
            existingUser.setVerified(true);
            userRepository.save(existingUser);
            return "User updated successfully!";
        } else {
            return "User not found!";
        }
    }
}
