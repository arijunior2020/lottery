package com.lottery.marketplace.domain.lottery;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@UtilityClass
public class LotterySpecification {

    public static Specification<Lottery> hasStatus(List<LotteryStatus> statusList) {
        return (lottery, cq, cb) -> lottery.get("status").in(statusList);
    }

}