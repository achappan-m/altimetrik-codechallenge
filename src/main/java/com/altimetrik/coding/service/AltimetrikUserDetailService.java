package com.altimetrik.coding.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.altimetrik.coding.model.AltimetrikUser;
import com.altimetrik.coding.model.Request;


public interface AltimetrikUserDetailService {

    Optional<User> getToken(String token);
    AltimetrikUser generateToken(Request user) throws UsernameNotFoundException;
    boolean validateToken(String token);
    UserDetails loadUserByUsername(String username);
    String readRefreshToken(String tokenValue);
    void saveRefreshToken(String token, Request userInput);
    
    
}
