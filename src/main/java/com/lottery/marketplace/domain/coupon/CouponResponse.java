package com.lottery.marketplace.domain.coupon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CouponResponse {
  private UUID id;

  private String name;

  private Double discountPercentage;

  private Boolean isUnlimited;

  private Long quantity;

  private Long appliedQuantity;

  private Long remanescentQuantity;

  private CouponStatus couponStatus;

  private CouponPeriod couponPeriod;

  private ZonedDateTime createdDate = ZonedDateTime.now();

  private ZonedDateTime updatedDate;

  private Boolean isExpired;

}
