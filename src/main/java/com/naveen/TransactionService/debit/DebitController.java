package com.naveen.TransactionService.debit;

import com.naveen.TransactionService.dto.AuthorizationRequest;
import com.naveen.TransactionService.dto.AuthorizationResponse;
import com.naveen.TransactionService.dto.LoadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import java.util.Set;

@RestController
@RequestMapping("/authorization")
@Validated
public class DebitController {

    @Autowired
    private DebitService debitService;

    @PutMapping("/{messageId}")
    public ResponseEntity<AuthorizationResponse> debitBalanceFromUser(
            @PathVariable String messageId,
            @RequestBody AuthorizationRequest authorizationRequest) {

        validateRequest(authorizationRequest);
        AuthorizationResponse authorizationResponse = debitService.debitTransaction(authorizationRequest);
        return new ResponseEntity<>(authorizationResponse, HttpStatus.CREATED);

    }

    private void validateRequest(AuthorizationRequest authorizationRequest) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<AuthorizationRequest>> violations = validator.validate(authorizationRequest);
        if(violations.size() > 0) {
            throw new ConstraintViolationException(violations);
        }
    }
}
