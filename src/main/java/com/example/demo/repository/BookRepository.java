package com.example.demo.repository;

import com.example.demo.domain.Book;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BookRepository extends PagingAndSortingRepository<Book, Long> {

    Book findBookByIdentifier(String identifier);
    List<Book> findBooksByAuthor(String author);
    Book findBookById(long id);
}
