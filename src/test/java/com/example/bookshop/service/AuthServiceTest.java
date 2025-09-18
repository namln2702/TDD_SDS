package com.example.bookshop.service;


import com.example.bookshop.dto.LoginRequest;
import com.example.bookshop.dto.LoginResponse;
import com.example.bookshop.model.User;
import com.example.bookshop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;



    @Test
    void should_login_successful_with_correct_info() {
        // Arrange
        String username = "user1";
        String password = "password1";
        LoginRequest loginRequest = new LoginRequest(username, password);

        User user = User.builder()
                .username(username)
                .password(password)
                .build();
        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        // Act
        LoginResponse result = authService.login(loginRequest);

        // Assert
        assertThat(result, notNullValue());
        assertThat(username, is(result.username()));
    }

    @Test
    void should_throw_exception_when_username_not_found() {
        // Arrange
        String username = "unknownUser";
        String password = "anyPassword";
        LoginRequest loginRequest = new LoginRequest(username, password);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));

        assertThat(exception.getMessage(), is("Username not found"));
    }

    @Test
    void should_throw_exception_when_password_not_match() {
        // Arrange
        String username = "user1";
        String correctPassword = "password1";
        String wrongPassword = "wrongPassword";
        LoginRequest loginRequest = new LoginRequest(username, wrongPassword);
        User user = User.builder()
                .username(username)
                .password(correctPassword)
                .build();

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });

        assertThat(exception.getMessage(), is("Password not match"));
    }

    @Test
    void should_throw_exception_when_username_is_null() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest(null, "password");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));

        assertThat(exception.getMessage(), is("Username cannot be null or empty"));
    }

    @Test
    void should_throw_exception_when_password_is_null() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("user1", null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));

        assertThat(exception.getMessage(), is("Password cannot be null or empty"));
    }

    @Test
    void should_throw_exception_when_username_is_empty() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("", "password");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));

        assertThat(exception.getMessage(), is("Username cannot be null or empty"));
    }

    @Test
    void should_throw_exception_when_password_is_empty() {
        // Arrange
        String username = "user1";
        String password = "";
        LoginRequest loginRequest = new LoginRequest(username, password);



        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));

        assertThat(exception.getMessage(), is("Password cannot be null or empty"));
    }

    @Test
    void should_trim_username_before_login() {
        // Arrange
        String username = "user1";
        String password = "password1";
        LoginRequest loginRequest = new LoginRequest("  user1  ", password);

        User user = User.builder()
                .username(username)
                .password(password)
                .build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        LoginResponse result = authService.login(loginRequest);

        // Assert
        assertThat(result, notNullValue());
        assertThat(result.username(), is(username));
    }


}
