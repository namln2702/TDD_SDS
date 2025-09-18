package com.example.bookshop.dto;


import lombok.Builder;

@Builder
public record CheckoutCartRequest(
        Long cartId
) {
}
