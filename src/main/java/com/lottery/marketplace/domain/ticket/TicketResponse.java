package com.lottery.marketplace.domain.ticket;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class TicketResponse implements Serializable {

    private UUID id;

    private String ticketNumber;

    private BigDecimal ticketValue;

    private BigDecimal ticketValueTotal;

    private Long ticketAmount;

    private UUID lotteryId;

    private Long lotteryNumber;

    private UUID userId;

    @Column(name = "created_date", updatable = false)
    private ZonedDateTime createdDate = ZonedDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.PENDING;

    private ZonedDateTime expirationDate;

    private String qrCodeBase64;

    private String qrCode;

    private String qrCodeLink;

    private String userIdentification;

    private String userFirstName;

    private String userLastName;

    private List<String> luckNumber;

}
