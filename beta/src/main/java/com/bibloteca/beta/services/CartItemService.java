package com.bibloteca.beta.services;

import com.bibloteca.beta.entities.Book;
import com.bibloteca.beta.entities.CartItem;
import com.bibloteca.beta.entities.Customer;
import com.bibloteca.beta.repositories.CartItemRepository;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {

    private CartItemRepository cartItemRepository;
    private final BookService bookService;

    @Autowired
    public CartItemService(CartItemRepository cartItemRepository, BookService bookService) {
        this.cartItemRepository = cartItemRepository;
        this.bookService = bookService;
    }

    @Transactional
    public List<CartItem> getCart(Customer customer) {
        return cartItemRepository.findByCustomer(customer);
    }

    @Transactional
    public Double getTotal(Customer customer) {
        List<CartItem> cart = cartItemRepository.findByCustomer(customer);
        Double total = 0.0;

        for (CartItem item : cart) {
            total += item.getBook().getPrice() * item.getQuantity();
        }
        return total;
    }

    @Transactional
    public void addBook(Customer customer, String id) throws Exception {
        Book book = bookService.findById(id);

        Optional<CartItem> optional = cartItemRepository.findByCustomerAndBook(customer, book);

        if (optional.isPresent()) {//normalizo un optional a un CartItem
            CartItem item = optional.get();

            if (item.getQuantity() >= book.getStock()) {
                throw new Exception("Stock insuficiente para: "+ book.getName());
            }
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);//Si ya se agrego ese libro al carrito aumento quantity
        } else {
            if (book.getStock() < 1) {
                throw new Exception("Stock insuficiente para: " + book.getName());
            }
            CartItem item = new CartItem();
            item.setBook(book);
            item.setCustomer(customer);
            item.setQuantity(1);

            cartItemRepository.save(item);//Si es un nuevo libro que ingresa al carrito, lo seteo de cero
        }
    }

    @Transactional
    public void removeBook(Customer customer, String id) throws Exception {

        Book book = bookService.findById(id);

        CartItem item = cartItemRepository.findByCustomerAndBook(customer, book).orElseThrow(() -> new Exception("Libro inexistente en el carrito"));

        if (item.getQuantity() == 1) {
            System.out.println("entro a cartItem");
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(item.getQuantity() - 1);
            System.out.println("tmb entro a cartItem");
            cartItemRepository.save(item);
        }
    }

    @Transactional
    public void clearCart(Customer customer) {
        cartItemRepository.deleteByCustomer(customer);
    }
}
