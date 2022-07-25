package com.naveen.TransactionService.credit;

import com.naveen.TransactionService.dto.LoadRequest;
import com.naveen.TransactionService.dto.LoadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import java.util.Set;

@RestController
@RequestMapping("/load")
@Slf4j
public class CreditController {

    @Autowired
    private CreditService creditService;

    @PutMapping("/{messageId}")
    public ResponseEntity<LoadResponse> creditBalanceToUser(
            @PathVariable String messageId, @RequestBody LoadRequest loadRequest) {

        validateRequest(loadRequest);
        LoadResponse loadResponse = creditService.createCreditTransaction(loadRequest);
        return new ResponseEntity<>(loadResponse, HttpStatus.CREATED);

    }

    private void validateRequest(LoadRequest loadRequest) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<LoadRequest>> violations = validator.validate(loadRequest);
        if(violations.size() > 0) {
            throw new ConstraintViolationException(violations);
        }
    }

}
