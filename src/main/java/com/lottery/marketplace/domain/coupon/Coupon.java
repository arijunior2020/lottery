package com.lottery.marketplace.domain.coupon;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "coupons")
public class Coupon implements Serializable {
  @Id
  @GeneratedValue(generator = "UUID")
  @Column(name = "id", insertable = false, updatable = false, nullable = false)
  private UUID id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "discount_percentage",nullable = false)
  private Double discountPercentage;

  @Column(name = "is_unlimited",nullable = false)
  private Boolean isUnlimited;

  @Column(name = "quantity")
  private Long quantity;

  @Column(name = "applied_quantity")
  private Long appliedQuantity;

  @Column(name = "remanascent_quantity")
  private Long remanescentQuantity;

  @Column(name = "status",nullable = false)
  private CouponStatus couponStatus;

  @Column(name = "period_start",nullable = false)
  private ZonedDateTime periodStart;

  @Column(name = "period_end",nullable = false)
  private ZonedDateTime periodEnd;

  @Column(name = "created_date", columnDefinition = "TIMESTAMPTZ")
  private ZonedDateTime createdDate = ZonedDateTime.now();

  @Column(name = "updated_date", columnDefinition = "TIMESTAMPTZ")
  private ZonedDateTime updatedDate;

  @Column(name = "is_expired")
  private Boolean isExpired;
}
