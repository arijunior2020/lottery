package com.lottery.marketplace.domain.payment;

import com.lottery.marketplace.domain.ticket.Ticket;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequest {

    @NotBlank
    private Ticket ticket;

    @NotNull
    private PaymentProviders paymentProviders;
}
