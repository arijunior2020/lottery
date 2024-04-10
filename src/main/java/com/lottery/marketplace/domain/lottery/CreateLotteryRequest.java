package com.lottery.marketplace.domain.lottery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class CreateLotteryRequest {

    @NotNull
    private String title;

    @NotBlank
    private String subTitle;

    @NotBlank
    private String description;

    @NotNull
    private String regulation;

    @NotBlank
    private String bannerImageUrl;

    @NotBlank
    private String bannerPhoneImageUrl;

    @NotBlank
    private String thumbnailImageUrl;

    @NotNull
    private Long maxQuantity;

    private Long minQuantity;

    @NotNull
    private BigDecimal ticketValue;

    @NotNull
    private ZonedDateTime executionDate;

    private List<String> photoLinks;

    private List<String> photoPhoneLinks;

    @NotNull
    private Long ticketQuantity;

    private Long checkoutTimeMinutes = 10L;

    private Long winnersQuantity = 1L;

    private List<String> luckNumber;

    private boolean showRanking;
}
