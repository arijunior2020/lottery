package com.lottery.marketplace.domain.lottery;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class LotteryResponse implements Serializable {

    private UUID id;

    private Long lotteryNumber;

    private String title;

    private String subTitle;

    private String description;

    private String regulation;

    private String bannerImageUrl;

    private String bannerPhoneImageUrl;

    private String thumbnailImageUrl;

    private Long maxQuantity;

    private Long minQuantity;

    private BigDecimal ticketValue;

    private LotteryStatus status;

    private ZonedDateTime executionDate;

    private ZonedDateTime createdDate;

    private List<String> photoLinks;

    private List<String> photoPhoneLinks;

    private Long ticketQuantity;

    private Long checkoutTimeMinutes;

    private Long winnersQuantity;

    private List<String> luckNumber;

    private boolean showRanking;

    private Map<String, String> promotions = new HashMap<>();
}
