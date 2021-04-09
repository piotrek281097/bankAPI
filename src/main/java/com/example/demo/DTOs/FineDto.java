package com.example.demo.DTOs;

import com.example.demo.domain.Reader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FineDto {

    private Long id;

    private String text;

    private Double money;

    private Reader reader;

}
