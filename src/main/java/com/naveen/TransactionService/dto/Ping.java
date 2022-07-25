package com.naveen.TransactionService.dto;

import lombok.Data;
import lombok.Value;

@Data
@Value(staticConstructor = "of")
public class Ping {

    String serverTime;

}
