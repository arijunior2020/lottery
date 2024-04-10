package com.lottery.marketplace.domain.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private String email;

    private String identification;

    private String name;
}