package com.example.bookshop.service;

import com.example.bookshop.dto.CartItemRequest;
import com.example.bookshop.dto.CreateCartRequest;
import com.example.bookshop.enums.StatusUser;
import com.example.bookshop.model.Book;
import com.example.bookshop.model.Cart;
import com.example.bookshop.model.CartItem;
import com.example.bookshop.model.User;
import com.example.bookshop.repository.BookRepository;
import com.example.bookshop.repository.CartRepository;
import com.example.bookshop.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.hamcrest.Matchers.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    CartRepository cartRepository;

    @Mock
    BookRepository bookRepository;

    @InjectMocks
    CartService cartService;

    @Test
    void should_throw_exception_when_user_is_not_login() {
        // Arrange
        Long userId = 2L;
        CreateCartRequest createCartRequest = new CreateCartRequest(userId);
        User inactiveUser = User.builder()
                .userId(userId)
                .username("userInactive")
                .status(StatusUser.inActive) // 👈 trạng thái inActive
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(inactiveUser));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cartService.createCartForUser(createCartRequest));

        assertThat(ex.getMessage(), is("User not active"));
    }

    @Test
    void should_create_cart_successful_for_user() {
        // Arrange
        Long userId = 1L;
        CreateCartRequest createCartRequest = new CreateCartRequest(userId);
        User user = User.builder()
                .userId(userId)
                .username("user1")
                .status(StatusUser.active)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Cart cart = cartService.createCartForUser(createCartRequest);

        // Assert
        assertThat(cart, notNullValue());
    }

    @Test
    void should_return_exception_when_user_not_found() {
        // Arrange
        Long userId = 10000L;
        CreateCartRequest createCartRequest = new CreateCartRequest(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Exception exception = assertThrows(RuntimeException.class,
                () -> cartService.createCartForUser(createCartRequest));

        // Assert
        assertThat(exception.getMessage(), is("User not found"));
    }

    @Test
    void should_add_new_product_to_cart_when_not_exist_in_cart() {
        // Arrange
        Long cartId = 1L;
        Long bookId = 100L;
        int quantityToAdd = 5;

        CartItemRequest cartItemRequest = new  CartItemRequest(cartId, bookId, quantityToAdd);

        Book book = new Book();
        book.setBookId(bookId);
        book.setStockQuantity(10);

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Cart result = cartService.addProductToCart(cartItemRequest);

        // Assert
        assertThat(result, notNullValue());
        assertThat(result.getItems().size(), is(1) );

        CartItem item = result.getItems().get(0);
        assertThat(item.getBook(), is(book));
        assertThat(item.getQuantity(), is(quantityToAdd) );
    }

    @Test
    void should_update_quantity_when_product_already_in_cart_and_not_exceed_stock() {
        // Arrange
        Long cartId = 1L;
        Long bookId = 100L;
        int quantityToAdd = 4;

        CartItemRequest cartItemRequest = new CartItemRequest(cartId, bookId, quantityToAdd);

        Book book = new Book();
        book.setBookId(bookId);
        book.setStockQuantity(10);

        CartItem existingItem = new CartItem();
        existingItem.setBook(book);
        existingItem.setQuantity(3);

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(List.of(existingItem)));

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));



        // Act
        Cart result = cartService.addProductToCart(cartItemRequest);

        // Assert
        assertThat(result, notNullValue());
        assertThat(result.getItems().size(), is(1));
        CartItem item = result.getItems().get(0);
        assertThat(item.getBook(), is(book));
        assertThat(item.getQuantity(), is(7));
    }

    @Test
    void should_limit_quantity_to_stock_when_exceed_stock() {
        // Arrange
        Long cartId = 1L;
        Long bookId = 100L;
        int quantityToAdd = 5;

        CartItemRequest cartItemRequest = new CartItemRequest(cartId, bookId, quantityToAdd);

        Book book = new Book();
        book.setBookId(bookId);
        book.setStockQuantity(10);

        CartItem existingItem = new CartItem();
        existingItem.setBook(book);
        existingItem.setQuantity(8);

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(List.of(existingItem)));

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));



        // Act
        Cart result = cartService.addProductToCart(cartItemRequest);

        // Assert


        assertThat(result, notNullValue());
        assertThat(result.getItems().size(), is(1));
        CartItem item = result.getItems().get(0);
        assertThat(item.getBook(), is(book));
        assertThat(item.getQuantity(), is(10));
    }

    @Test
    void should_throw_exception_when_cart_not_found() {
        // Arrange
        Long cartId = 1L;
        Long bookId = 100L;
        int quantityToAdd = 1;

        CartItemRequest cartItemRequest = new CartItemRequest(cartId, bookId, quantityToAdd);

        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            cartService.addProductToCart(cartItemRequest);
        });

        assertThat(ex.getMessage() , is("Cart not found"));
    }

    @Test
    void should_throw_exception_when_book_not_found() {
        // Arrange
        Long cartId = 1L;
        Long bookId = 100L;
        int quantityToAdd = 1;

        CartItemRequest cartItemRequest = new CartItemRequest(cartId, bookId, quantityToAdd);

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            cartService.addProductToCart(cartItemRequest);
        });

        assertThat(ex.getMessage() , is("Book not found"));
    }
}
