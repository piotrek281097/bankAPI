package com.example.demo.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CurrencyDto {

    private Map<String, Double> rates;

    private String base;

    private String date;

}
