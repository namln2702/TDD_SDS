package com.example.bookshop.dto;


import lombok.Builder;

@Builder
public record LoginRequest(
        String username,
        String password
) {
}
