package com.lottery.marketplace.domain.ticket;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Ranking implements Serializable {

    private String userEmail;
    private BigDecimal totalValue;
    private String firstName;
    private String lastName;
}