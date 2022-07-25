package com.naveen.TransactionService.health;

import com.naveen.TransactionService.dto.Ping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/ping")
public class PingController {

    @GetMapping
    public ResponseEntity<Ping> healthCheck() {

        return new ResponseEntity<>(Ping.of(Instant.now().toString()), HttpStatus.OK);

    }

}
