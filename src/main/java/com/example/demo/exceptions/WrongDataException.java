package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class WrongDataException extends RuntimeException {

    public WrongDataException(String message) {
        super(message);
    }

}
