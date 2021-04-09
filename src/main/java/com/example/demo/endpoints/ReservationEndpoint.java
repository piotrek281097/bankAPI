package com.example.demo.endpoints;

import com.example.demo.DTOs.ReservationDto;
import com.example.demo.domain.Reservation;
import com.example.demo.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/")
public class ReservationEndpoint {

    private ReservationService reservationService;

    @Autowired
    public ReservationEndpoint(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("reservations/add")
    public ResponseEntity<Reservation> addReservation(@RequestBody ReservationDto reservationDto) {
        return new ResponseEntity<Reservation>(reservationService.addReservation(reservationDto), HttpStatus.OK);
    }

    @PostMapping("reservations/update")
    public ResponseEntity<Reservation> updateReader(@RequestBody ReservationDto reservationDto) {
        return new ResponseEntity<Reservation>(reservationService.updateReservation(reservationDto), HttpStatus.OK);
    }

}
