package com.example.bookshop.dto;


import lombok.Builder;

@Builder
public record CreateCartRequest(
        Long userId

) {
}
