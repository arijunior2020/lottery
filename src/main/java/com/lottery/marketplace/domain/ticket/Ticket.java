package com.lottery.marketplace.domain.ticket;

import com.lottery.marketplace.config.StringListConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Data
public class Ticket implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private UUID id;

    @Column(columnDefinition = "text")
    private String ticketNumber;

    @Column(nullable = false, updatable = false)
    private BigDecimal ticketValue;

    @Column(nullable = false, updatable = false)
    private BigDecimal ticketValueTotal;

    @Column(nullable = false, updatable = false)
    private Long ticketAmount;

    @Column(name="lottery_id")
    private UUID lotteryId;

    @Column(nullable = false, updatable = false)
    private Long lotteryNumber;

    @Column(name="user_id")
    private UUID userId;

    @Column(nullable = false, updatable = false)
    private String userIdentification;

    @Column(nullable = false, updatable = false)
    private String userFirstName;

    @Column(nullable = false, updatable = false)
    private String userLastName;

    @Column(nullable = false, updatable = false)
    private String userPhone;

    @Column(nullable = false, updatable = false)
    private String userEmail;

    @Column(name = "created_date", updatable = false)
    private ZonedDateTime createdDate = ZonedDateTime.now();

    @Column(name = "expiration_date", updatable = false, nullable = false)
    private ZonedDateTime expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.PENDING;

    @Column(name="transaction_id")
    private String transactionId;

    @Column(columnDefinition = "text")
    private String qrCodeBase64;

    @Column(columnDefinition = "text")
    private String qrCode;

    @Column(columnDefinition = "text")
    private String qrCodeLink;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "text")
    private List<String> luckNumber;
}