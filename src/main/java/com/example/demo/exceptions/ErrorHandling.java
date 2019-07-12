package com.example.demo.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class ErrorHandling {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandling.class);

    @ExceptionHandler(AccountDoesNotExistException.class)
    public void AccountDoesNotExistException(AccountDoesNotExistException exc, HttpServletResponse response) throws IOException {
        LOGGER.error(exc.getMessage());
        response.sendError(HttpStatus.NOT_FOUND.value(), exc.getMessage());
    }

    @ExceptionHandler(ConnectionException.class)
    public void ConnectionException(ConnectionException exc, HttpServletResponse response) throws IOException {
        LOGGER.error(exc.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), exc.getMessage());
    }

    @ExceptionHandler(CurrencyIsNotAvailableException.class)
    public void CurrencyIsNotAvailableException(CurrencyIsNotAvailableException exc, HttpServletResponse response) throws IOException {
        LOGGER.error(exc.getMessage());
        response.sendError(HttpStatus.NOT_FOUND.value(), exc.getMessage());
    }

    @ExceptionHandler(NotEnoughMoneyToMakeTransferException.class)
    public void NotEnoughMoneyToMakeTransferException(NotEnoughMoneyToMakeTransferException exc, HttpServletResponse response) throws IOException {
        LOGGER.error(exc.getMessage());
        response.sendError(HttpStatus.CONFLICT.value(), exc.getMessage());
    }

    @ExceptionHandler(AccountWithThisNumberAlreadyExistsException.class)
    public void AccountWithThisNumberAlreadyExistsException(AccountWithThisNumberAlreadyExistsException exc, HttpServletResponse response) throws IOException {
        LOGGER.error(exc.getMessage());
        response.sendError(HttpStatus.CONFLICT.value(), exc.getMessage());
    }

}