package com.example.bookshop.service;

import com.example.bookshop.dto.CheckoutCartRequest;
import com.example.bookshop.model.Book;
import com.example.bookshop.model.Cart;
import com.example.bookshop.model.CartItem;
import com.example.bookshop.model.Order;
import com.example.bookshop.repository.BookRepository;
import com.example.bookshop.repository.CartRepository;
import com.example.bookshop.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderService {

    BookRepository bookRepository;
    CartRepository cartRepository;
    OrderRepository orderRepository;

    @Transactional
    public void checkoutCart(CheckoutCartRequest checkoutCartRequest) {
        Cart cart = cartRepository.findById(checkoutCartRequest.cartId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            int stock = book.getStockQuantity();
            int quantity = item.getQuantity();

            if (quantity > stock) {
                throw new RuntimeException("Not enough stock for book: " + book.getTitle());
            }

            book.setStockQuantity(stock - quantity);
            bookRepository.save(book);

            Order order = new Order();
            order.setBook(book);
            order.setQuantity(quantity);
            orderRepository.save(order);
        }

        cart.getItems().clear();
        cartRepository.save(cart);
    }

}
