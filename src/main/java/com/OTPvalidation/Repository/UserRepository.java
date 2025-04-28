package com.OTPvalidation.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.OTPvalidation.Entity.User;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email); // Custom query method to find user by email
    User findByPhoneNumber(String phoneNumber); // Custom query method to find user by phone number
    List<User> findByName(String name); // Custom query method to find user by name
}
