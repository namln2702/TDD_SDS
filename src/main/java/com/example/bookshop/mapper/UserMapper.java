package com.example.bookshop.mapper;

import com.example.bookshop.dto.LoginResponse;
import com.example.bookshop.model.User;
import org.springframework.stereotype.Component;

public class UserMapper {

    public static LoginResponse toLoginResponse(User user) {
        return new LoginResponse(
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getAddress(),
                user.getPhone()
        );
    }
}
