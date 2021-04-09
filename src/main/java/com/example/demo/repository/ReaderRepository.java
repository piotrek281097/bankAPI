package com.example.demo.repository;

import com.example.demo.domain.Reader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ReaderRepository extends PagingAndSortingRepository<Reader, Long> {

    Page<Reader> findAll (Pageable pageable);

    Reader findByPesel(String pesel);
}
