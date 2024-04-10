package com.lottery.marketplace.domain.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CreateUserRequest {

    @NotBlank( message = "É obrigatório preencher o email.")
    private String email;

    @NotBlank( message = "É obrigatório preencher a senha.")
    private String password;

    @NotBlank( message = "É obrigatório preencher o CPF.")
    private String identification;

    @NotBlank( message = "É obrigatório preencher o nome.")
    private String name;

    @NotNull
    private String lastName;

    @NotBlank( message = "É obrigatório preencher o número telefone.")
    private String phone;

    private String dateOfBirthday;

    private String addressCep;

    private String addressStreet;

    private String addressNumber;

    private String addressComplement;

    private String addressCity;

    private String addressState;
}