package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class TransferCantBeCanceledException extends RuntimeException {

    public TransferCantBeCanceledException(String message) {
        super(message);
    }
}