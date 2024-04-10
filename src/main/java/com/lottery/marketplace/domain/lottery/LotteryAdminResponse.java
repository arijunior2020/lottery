package com.lottery.marketplace.domain.lottery;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class LotteryAdminResponse implements Serializable {

    private Lottery lottery;

    private Long soldTickets;

    private BigDecimal soldTicketsValue;
}
