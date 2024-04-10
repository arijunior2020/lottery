package com.lottery.marketplace.domain.coupon;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import javax.annotation.Nullable;

@Data
public class CreateCouponRequest {

  @NotBlank(message = "O nome deve vir preenchido.")
  private String name;

  @NotNull(message = "A porcentagem de desconto deve ser informada.")
  private Double discountPercentage;

  private Boolean isUnlimited;

  @NotNull(message = "Quantidade deve ser no mínimo um.")
  private Long quantity;

  @NotNull(message = "Status deve ser ativo ou inativo.")
  private Integer couponStatus;

  @NotNull
  private CouponPeriod couponPeriod;

  @Nullable
  private Boolean isExpired;
}
