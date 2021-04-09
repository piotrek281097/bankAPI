package com.example.demo.service;

import com.example.demo.domain.Book;
import com.example.demo.exceptions.BookWithThisIdentifierAlreadyExistsException;
import com.example.demo.exceptions.WrongDataException;
import com.example.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void addBook(Book book) {
        try {
            Book bookFoundByIdentifier = findBookByIdentifier(book.getIdentifier());

            if (bookFoundByIdentifier == null) {
                bookRepository.save(book)
                ;
            } else {
                throw new BookWithThisIdentifierAlreadyExistsException("Book with this identifier already exists");
            }
        } catch (ConstraintViolationException | IllegalArgumentException ex) {
            throw new WrongDataException("Wrong data");
        }
    }

    @Override
    public void updateBook(long bookId, Book book) {
        bookRepository.save(book);
    }

    @Override
    public List<Book> getAllBooks() {
        return null;
    }

    @Override
    public Book findBookByIdentifier(String identifier) {
        Book book = bookRepository.findBookByIdentifier(identifier);
        return book;
    }

    @Override
    public Book findBookById(long bookId) {
        return bookRepository.findBookById(bookId);
    }

    @Override
    public void deleteBookById(long bookId) {
        bookRepository.deleteById(bookId);
    }

    @Override
    public List<Book> findBooksByAuthor(String author) {
        return bookRepository.findBooksByAuthor(author);
    }

    @Override
    public List<Book> findBooksByReaderId(long readerId) {
        return null;
    }

    @Override
    public Page<Book> getPageableBooks(int page, int size) {
        return bookRepository.findAll(PageRequest.of(page, size));
    }
}
