package com.OTPvalidation.Service;

import java.util.List;

import com.OTPvalidation.Entity.User;

public interface UserService {
    String addUser(User user); // Method to add a user
    List<User> getUser(); // Method to get a user by ID
    User getUserByEmail(String email); // Method to get a user by email
    String updateUser(User user); // Method to update a user by email
}
