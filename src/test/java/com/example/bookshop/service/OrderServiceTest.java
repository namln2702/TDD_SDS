package com.example.bookshop.service;

import com.example.bookshop.dto.CheckoutCartRequest;
import com.example.bookshop.model.Book;
import com.example.bookshop.model.Cart;
import com.example.bookshop.model.CartItem;
import com.example.bookshop.model.Order;
import com.example.bookshop.repository.BookRepository;
import com.example.bookshop.repository.CartRepository;
import com.example.bookshop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class OrderServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;


    @Test
    void should_reduce_stock_when_order_is_confirmed() {
        // Arrange
        Long cartId = 1L;
        Long bookId = 10L;

        Book book = new Book();
        book.setBookId(bookId);
        book.setTitle("Domain-Driven Design");
        book.setStockQuantity(8); // tồn kho ban đầu = 8

        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(3); // đặt 3 cuốn

        Cart cart = new Cart();
        cart.setCartId(cartId);
        cart.setItems(new ArrayList<>(List.of(cartItem)));

        CheckoutCartRequest request = new CheckoutCartRequest(cartId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        orderService.checkoutCart(request);

        // Assert với assertThat
        assertThat(book.getStockQuantity(), is(5));               // stock giảm từ 8 -> 5
        assertThat(cart.getItems(), is(empty()));                 // cart rỗng sau khi checkout

        // Verify các repository được gọi
        verify(bookRepository).save(book);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void should_checkout_successfully_when_stock_is_enough() {
        // Arrange
        Long cartId = 1L;
        Book book = new Book();
        book.setBookId(1L);
        book.setTitle("Book 1");
        book.setStockQuantity(10);

        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(5);

        Cart cart = new Cart();
        cart.setCartId(cartId);
        cart.setItems(new ArrayList<>(List.of(cartItem)));

        CheckoutCartRequest request = new CheckoutCartRequest(cartId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        orderService.checkoutCart(request);

        // Assert
        assertEquals(5, book.getStockQuantity());

        assertTrue(cart.getItems().isEmpty());

        verify(cartRepository).findById(cartId);
        verify(bookRepository).save(book);
        verify(orderRepository).save(any(Order.class));
        verify(cartRepository).save(cart);
    }

    @Test
    void should_throw_exception_when_stock_is_not_enough() {
        // Arrange
        Long cartId = 1L;
        Book book = new Book();
        book.setBookId(1L);
        book.setTitle("Book 1");
        book.setStockQuantity(3);

        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(5); // more than stock

        Cart cart = new Cart();
        cart.setCartId(cartId);
        cart.setItems(new ArrayList<>(List.of(cartItem)));

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        CheckoutCartRequest request = new CheckoutCartRequest(cartId);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.checkoutCart(request));
        assertEquals("Not enough stock for book: Book 1", ex.getMessage());

        // Verify bookRepository.save() and orderRepository.save() never called
        verify(bookRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void should_throw_exception_when_cart_not_found() {
        // Arrange
        Long cartId = 99L;
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        CheckoutCartRequest request = new CheckoutCartRequest(cartId);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.checkoutCart(request));
        assertEquals("Cart not found", ex.getMessage());
    }
}

