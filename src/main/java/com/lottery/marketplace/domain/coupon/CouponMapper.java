package com.lottery.marketplace.domain.coupon;

import org.springframework.stereotype.Component;

@Component
public class CouponMapper {
  public Coupon toEntity(CreateCouponRequest coupon) {
    if (coupon == null) {
      return null;
    }

    return Coupon.builder()
      .name(coupon.getName())
      .discountPercentage(coupon.getDiscountPercentage())
      .isUnlimited(coupon.getIsUnlimited())
      .quantity(coupon.getQuantity())
      .couponStatus(coupon.getCouponStatus().equals(0) ? CouponStatus.INACTIVE : CouponStatus.ACTIVE)
      .periodStart(coupon.getCouponPeriod().getPeriodStart())
      .periodEnd(coupon.getCouponPeriod().getPeriodEnd())
      .isExpired(coupon.getIsExpired())
      .build();
  }

  public Coupon toEntity(CreateCouponUpdateRequest couponUpdateRequest) {
    if (couponUpdateRequest == null) {
      return null;
    }
    return Coupon.builder()
      .isUnlimited(couponUpdateRequest.getIsUnlimited())
      .quantity(couponUpdateRequest.getQuantity())
      .couponStatus(couponUpdateRequest.getCouponStatus().equals(0) ? CouponStatus.INACTIVE : CouponStatus.ACTIVE)
      .periodStart(couponUpdateRequest.getCouponPeriod().getPeriodStart())
      .periodEnd(couponUpdateRequest.getCouponPeriod().getPeriodEnd())
      .build();
  }

  public CouponResponse toResponse(Coupon savedCoupon) {
    return CouponResponse.builder()
      .id(savedCoupon.getId())
      .name(savedCoupon.getName())
      .discountPercentage(savedCoupon.getDiscountPercentage())
      .isUnlimited(savedCoupon.getIsUnlimited())
      .quantity(savedCoupon.getQuantity())
      .remanescentQuantity(savedCoupon.getRemanescentQuantity())
      .appliedQuantity(savedCoupon.getAppliedQuantity())
      .couponStatus(savedCoupon.getCouponStatus())
      .couponPeriod(CouponPeriod.builder()
        .periodStart(savedCoupon.getPeriodStart())
        .periodEnd(savedCoupon.getPeriodEnd())
        .build())
      .createdDate(savedCoupon.getCreatedDate())
      .updatedDate(savedCoupon.getUpdatedDate())
      .isExpired(savedCoupon.getIsExpired())
      .build();
  }
}
