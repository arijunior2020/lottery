package com.lottery.marketplace.domain.payment;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
public class PaymentResponse {

    private String id;

    private String externalReference;

    private BigDecimal totalValue;

    private String status;

    private String qrCodeBase64;

    private String qrCode;

    private String qrCodeLink;

    private OffsetDateTime expirationDate;

}
