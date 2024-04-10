package com.lottery.marketplace.domain.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserInformationRequest {

    @NotBlank
    private String name;

    @NotNull
    private String lastName;

    @NotBlank
    private String phone;
}
