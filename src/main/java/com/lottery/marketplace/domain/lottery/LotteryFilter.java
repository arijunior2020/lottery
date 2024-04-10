package com.lottery.marketplace.domain.lottery;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LotteryFilter implements Serializable {

    private List<LotteryStatus> statusList;
}
