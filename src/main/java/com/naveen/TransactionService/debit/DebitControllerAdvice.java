package com.naveen.TransactionService.debit;

import com.naveen.TransactionService.exceptions.InsufficientFundsException;
import com.naveen.TransactionService.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@RestControllerAdvice(basePackageClasses = DebitController.class)
@Slf4j
public class DebitControllerAdvice {

    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInsufficientFundsException(InsufficientFundsException ie) {
        log.error("log_type=TransactionService, errorCode={}", HttpStatus.BAD_REQUEST);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(new ErrorResponse(ie.getMessage()), headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        ConstraintViolation constraintViolation = e.getConstraintViolations().stream().iterator().next();
        String s = constraintViolation.getPropertyPath() + " " + constraintViolation.getMessage();
        return new ResponseEntity<>(new ErrorResponse(s), HttpStatus.BAD_REQUEST);
    }


}
