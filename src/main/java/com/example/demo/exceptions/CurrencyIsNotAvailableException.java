package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class CurrencyIsNotAvailableException extends RuntimeException {

    public CurrencyIsNotAvailableException(String message) {
        super(message);
    }
}
