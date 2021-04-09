package com.example.demo.repository;

import com.example.demo.domain.Reader;
import com.example.demo.domain.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long> {
    Page<Reservation> findAll (Pageable pageable);

    List<Reservation> findAllByReader(Reader reader);
}
