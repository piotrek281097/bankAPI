package com.example.demo.service;

import com.example.demo.DTOs.ReservationDto;
import com.example.demo.domain.Book;
import com.example.demo.domain.Reader;
import com.example.demo.domain.Reservation;
import com.example.demo.enums.ReservationStatus;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService{

    private ReservationRepository reservationRepository;

    private BookRepository bookRepository;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository, BookRepository bookRepository) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public Reservation addReservation(ReservationDto reservationDto) {
        Reservation reservation = new Reservation();
        fillReservation(reservationDto, reservation, false);


        return reservationRepository.save(reservation);
    }

    private void fillReservation(ReservationDto reservationDto, Reservation reservation, boolean update) {
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd").parse(reservationDto.getStartDate());
            endDate = new SimpleDateFormat("yyyy-MM-dd").parse(reservationDto.getEndDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (update) {
            reservation.setId(reservationDto.getId());
        }

        reservation.setStartDate(Optional.ofNullable(startDate).orElse(null));
        reservation.setEndDate(Optional.ofNullable(endDate).orElse(null));
        reservation.setReservationStatus(ReservationStatus.valueOf(reservationDto.getReservationStatus()));
        reservation.setBook(reservationDto.getBook());
        reservation.setReader(reservationDto.getReader());

        Optional<Book> bookById = bookRepository.findById(reservationDto.getBook().getId());

        if (bookById.isPresent()) {
            Book book = bookById.get();
            book.setBookStatus(reservationDto.getBook().getBookStatus());
            bookRepository.save(book);
        }
    }

    @Override
    public Reservation updateReservation(ReservationDto reservationDto) {
        Reservation reservation = new Reservation();
        fillReservation(reservationDto, reservation, true);

        return reservationRepository.save(reservation);
    }

    @Override
    public void deleteReservationById(long id) {
        reservationRepository.deleteById(id);
    }

    @Override
    public List<Reservation> findReservationsByReader(Reader reader) {
        return reservationRepository.findAllByReader(reader);
    }
}
