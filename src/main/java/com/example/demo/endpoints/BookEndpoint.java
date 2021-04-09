package com.example.demo.endpoints;

import com.example.demo.DTOs.PageableBookResponse;
import com.example.demo.domain.Book;
import com.example.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/")
public class BookEndpoint {

    private BookService bookService;

    @Autowired
    public BookEndpoint(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("books")
    public ResponseEntity<List<Book>> getBooks() {
        return new ResponseEntity<>(bookService.getAllBooks(), HttpStatus.OK);
    }

    @GetMapping("/books/pageable")
    public ResponseEntity<PageableBookResponse> retrieveBooks(@Param(value = "page") int page, @Param(value = "size") int size) {
        Page<Book> pageableBooks = bookService.getPageableBooks(page, size);
        PageableBookResponse pageableBookResponse = new PageableBookResponse(pageableBooks.getContent(),
                pageableBooks.getTotalPages(), page, size);
        return new ResponseEntity<>(pageableBookResponse, HttpStatus.OK);
    }

    @PostMapping("books/add")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        bookService.addBook(book);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @PutMapping("books/update/{bookId}")
    public ResponseEntity<Book> updateBook(@PathVariable long bookId, @RequestBody Book book) {
        bookService.updateBook(bookId, book);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    // 2 wersja
    @PutMapping("books/update")
    public ResponseEntity<Book> updateBook2(@RequestBody Book book) {
        bookService.updateBook(1, book);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("books/delete/{bookId}")
    public ResponseEntity<?> deleteBookId(@PathVariable long bookId) {
        bookService.deleteBookById(bookId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("books/find-book/{bookId}")
    public ResponseEntity<Book> findBookByBookId(@PathVariable long bookId) {
        return new ResponseEntity<>(bookService.findBookById(bookId), HttpStatus.OK);
    }

    @GetMapping("books/find-by-author/{author}")
    public ResponseEntity<List<Book>> findBooksByAuthor(@PathVariable String author) {
        return new ResponseEntity<>(bookService.findBooksByAuthor(author), HttpStatus.OK);
    }

    @GetMapping("books/find-by-reader/{readerId}")
    public ResponseEntity<List<Book>> findBooksByReaderId(@PathVariable long readerId) {
        return new ResponseEntity<>(bookService.findBooksByReaderId(readerId), HttpStatus.OK);
    }
}
