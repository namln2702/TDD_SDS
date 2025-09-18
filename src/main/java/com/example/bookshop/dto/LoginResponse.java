package com.example.bookshop.dto;


import lombok.Builder;

public record LoginResponse(
        Long userId,
        String username,
        String email,
        String fullName,
        String address,
        String phone
) {
}
