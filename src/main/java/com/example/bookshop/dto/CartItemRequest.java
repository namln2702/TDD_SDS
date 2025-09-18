package com.example.bookshop.dto;


import lombok.Builder;

@Builder
public record CartItemRequest(
        Long cartId,
        Long bookId,
        int quantity
) {
}
