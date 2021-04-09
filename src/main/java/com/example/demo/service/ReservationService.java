package com.example.demo.service;

import com.example.demo.DTOs.ReservationDto;
import com.example.demo.domain.Reader;
import com.example.demo.domain.Reservation;

import java.util.List;

public interface ReservationService {

    Reservation addReservation(ReservationDto reservation);

    Reservation updateReservation(ReservationDto reservation);

    void deleteReservationById(long id);

    List<Reservation> findReservationsByReader(Reader reader);

}
