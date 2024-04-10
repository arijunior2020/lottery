package com.lottery.marketplace.domain.coupon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponPeriod {
  private ZonedDateTime periodStart;
  private ZonedDateTime periodEnd;
}
