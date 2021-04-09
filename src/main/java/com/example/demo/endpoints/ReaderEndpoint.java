package com.example.demo.endpoints;

import com.example.demo.DTOs.PageableReaderResponse;
import com.example.demo.domain.Reader;
import com.example.demo.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/")
public class ReaderEndpoint {

    private ReaderService readerService;

    @Autowired
    public ReaderEndpoint(ReaderService readerService) {
        this.readerService = readerService;
    }

//    @GetMapping("readers")
//    public ResponseEntity<List<Book>> getBooks() {
//        return new ResponseEntity<>(bookService.getAllBooks(), HttpStatus.OK);
//    }

    @GetMapping("readers/pageable")
    public ResponseEntity<PageableReaderResponse> retrieveReaders(@Param(value = "page") int page, @Param(value = "size") int size) {
        Page<Reader> pageableReaders = readerService.getPageableReaders(page, size);
        PageableReaderResponse pageableReaderResponse = new PageableReaderResponse(pageableReaders.getContent(),
                pageableReaders.getTotalPages(), page, size);
        return new ResponseEntity<>(pageableReaderResponse, HttpStatus.OK);
    }

    @PostMapping("readers/add")
    public ResponseEntity<Reader> addReader(@RequestBody Reader reader) {
        readerService.addReader(reader);
        return new ResponseEntity<>(reader, HttpStatus.OK);
    }
//
//    @PutMapping("books/update/{bookId}")
//    public ResponseEntity<Book> updateBook(@PathVariable long bookId, @RequestBody Book reader) {
//        bookService.updateBook(bookId, reader);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
    // 2 wersja
    @PostMapping("readers/update")
    public ResponseEntity<Reader> updateReader(@RequestBody Reader reader) {
        readerService.updateReader(1, reader);
        return new ResponseEntity<>(reader, HttpStatus.OK);
    }

    @PutMapping("readers/delete/{id}")
    public ResponseEntity<?> deleteReaderById(@PathVariable long id) {
        readerService.deleteReaderById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("readers/find-reader/{id}")
    public ResponseEntity<Reader> findReaderById(@PathVariable long id) {
        return new ResponseEntity<>(readerService.findReaderById(id), HttpStatus.OK);
    }

    @GetMapping("books/find-by-pesel/{pesel}")
    public ResponseEntity<Reader> findReaderByPesel(@PathVariable String pesel) {
        return new ResponseEntity<>(readerService.findReaderByPesel(pesel), HttpStatus.OK);
    }
}
