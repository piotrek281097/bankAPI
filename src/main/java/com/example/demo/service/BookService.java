package com.example.demo.service;

import com.example.demo.domain.Book;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookService {

    void addBook(Book book);

    void updateBook(long bookId, Book book);

    List<Book> getAllBooks();

    Book findBookByIdentifier(String identifier);

    Book findBookById(long bookId);

    void deleteBookById(long bookId);

    List<Book> findBooksByAuthor(String ownerName);

    List<Book> findBooksByReaderId(long readerId);

    Page<Book> getPageableBooks(int page, int size);
}
