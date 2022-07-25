package com.naveen.TransactionService.credit;

import com.naveen.TransactionService.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@RestControllerAdvice(basePackageClasses = CreditController.class)
public class CreditControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        ConstraintViolation constraintViolation = e.getConstraintViolations().stream().iterator().next();
        String s = constraintViolation.getPropertyPath() + " "+constraintViolation.getMessage();
        return new ResponseEntity<>(new ErrorResponse(s), HttpStatus.BAD_REQUEST);
    }

}
