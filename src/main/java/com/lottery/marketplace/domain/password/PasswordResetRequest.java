package com.lottery.marketplace.domain.password;

import lombok.Data;

@Data
public class PasswordResetRequest {
  private String newPassword;
}
