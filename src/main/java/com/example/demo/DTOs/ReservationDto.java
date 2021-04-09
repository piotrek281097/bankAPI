package com.example.demo.DTOs;

import com.example.demo.domain.Book;
import com.example.demo.domain.Reader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {

    private Long id;

    private String startDate;

    private String endDate;

    private String reservationStatus;

    private Reader reader;

    private Book book;

}
