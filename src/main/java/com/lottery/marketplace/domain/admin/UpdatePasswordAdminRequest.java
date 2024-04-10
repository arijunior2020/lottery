package com.lottery.marketplace.domain.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePasswordAdminRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String newPassword;

}
