package com.example.bookshop.service;

import com.example.bookshop.dto.LoginRequest;
import com.example.bookshop.dto.LoginResponse;
import com.example.bookshop.mapper.UserMapper;
import com.example.bookshop.model.User;
import com.example.bookshop.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthService {

    UserRepository  userRepository;

    public LoginResponse login(LoginRequest loginRequest) {

        String username = loginRequest.username();
        String password = loginRequest.password();

        if(password == null  || password.isBlank() || password.isEmpty() )
            throw new RuntimeException("Password cannot be null or empty");
        if(username == null  ||  username.isBlank() || username.isEmpty() )
            throw new RuntimeException(("Username cannot be null or empty"));
        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new RuntimeException("Username not found"));
        if (!user.getPassword().equals(password))
            throw new RuntimeException("Password not match");

        return UserMapper.toLoginResponse(user);
    }
}
