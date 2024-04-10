package com.lottery.marketplace.domain.lottery;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class RankingResponse implements Serializable {

    private String firstName;

    private String lastName;

    private long position;

    private BigDecimal totalValue;

}
