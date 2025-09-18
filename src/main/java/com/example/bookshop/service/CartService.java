package com.example.bookshop.service;

import com.example.bookshop.dto.CartItemRequest;
import com.example.bookshop.dto.CreateCartRequest;
import com.example.bookshop.enums.StatusUser;
import com.example.bookshop.model.*;
import com.example.bookshop.repository.BookRepository;
import com.example.bookshop.repository.CartRepository;
import com.example.bookshop.repository.OrderRepository;
import com.example.bookshop.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
@RequiredArgsConstructor
public class CartService {

    CartRepository cartRepository;
    UserRepository userRepository;
    BookRepository bookRepository;
    OrderRepository orderRepository;

    public Cart createCartForUser(CreateCartRequest createCartRequest) throws RuntimeException{

        User user = userRepository.findById(createCartRequest.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(CheckUser(user)) {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
            return cartRepository.save(cart);
        }
        throw new RuntimeException("User not active");
    }

    public Cart addProductToCart(CartItemRequest cartItemRequest) {
        Cart cart = cartRepository.findById(cartItemRequest.cartId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        Book book = bookRepository.findById(cartItemRequest.bookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        int qtyToAdd = Math.min(cartItemRequest.quantity(), book.getStockQuantity());

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getBook().getBookId().equals(cartItemRequest.bookId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(Math.min(existingItem.getQuantity() + qtyToAdd, book.getStockQuantity()));
        } else {
            CartItem newItem = new CartItem();
            newItem.setBook(book);
            newItem.setQuantity(qtyToAdd);
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }


    public boolean CheckUser(User user){
        User user1 = userRepository.findById(user.getUserId()).orElseThrow(() -> new RuntimeException("User not active"));

        if(user1.getStatus() == StatusUser.active) return true;
        return false;
    }

}
