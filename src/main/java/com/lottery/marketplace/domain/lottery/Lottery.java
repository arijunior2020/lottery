package com.lottery.marketplace.domain.lottery;

import com.lottery.marketplace.config.JsonHashMapConverter;
import com.lottery.marketplace.config.StringListConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

@Entity
@Table(name = "lotteries")
@Data
public class Lottery implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private UUID id;

    @Column(name = "lottery_number", updatable = false, nullable = false)
    private Long lotteryNumber;

    private String title;

    private String subTitle;

    @Column(columnDefinition = "text")
    private String description;

    @Column(columnDefinition = "text")
    private String regulation;

    private String bannerImageUrl;

    private String bannerPhoneImageUrl;

    private String thumbnailImageUrl;

    @Column(nullable = false)
    private Long maxQuantity;

    private Long minQuantity = 1L;

    @Column(nullable = false)
    private BigDecimal ticketValue = BigDecimal.ONE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LotteryStatus status = LotteryStatus.ACTIVE;

    @Column(name = "execution_date", nullable = false)
    private ZonedDateTime executionDate;

    @Column(name = "created_date", updatable = false)
    private ZonedDateTime createdDate = ZonedDateTime.now();

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "text")
    private List<String> photoLinks;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "text")
    private List<String> photoPhoneLinks;

    @Column(columnDefinition = "BIGINT", nullable = false)
    private Long ticketQuantity = 999L;

    @Column(nullable = false)
    private Long checkoutTimeMinutes;

    @Column(nullable = false)
    private Long winnersQuantity;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "text")
    private List<String> luckNumber = new ArrayList<>();

    @Column(name = "show_ranking", nullable = false, columnDefinition = "boolean default true")
    private boolean showRanking = true;

    @Convert(converter = JsonHashMapConverter.class)
    @Column(columnDefinition = "text")
    private Map<String, String> promotions = new HashMap<>();
}