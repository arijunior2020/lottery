package com.lottery.marketplace.domain.coupon;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCouponUpdateRequest {
  private Boolean isUnlimited;

  @NotNull(message = "Quantidade deve ser no mínimo um.")
  private Long quantity;

  @NotNull(message = "Status deve ser ativo ou inativo.")
  private Integer couponStatus;
  @NotNull
  private CouponPeriod couponPeriod;
}
