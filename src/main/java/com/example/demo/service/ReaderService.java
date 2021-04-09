package com.example.demo.service;

import com.example.demo.domain.Reader;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReaderService {

    void addReader(Reader reader);

    void updateReader(long readerId, Reader reader);

    List<Reader> getAllReaders();

    Reader findReaderByPesel(String pesel);

    Reader findReaderById(long id);

    void deleteReaderById(long id);

    Page<Reader> getPageableReaders(int page, int size);

}
