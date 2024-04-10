package com.lottery.marketplace.domain.lottery;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LotteryMapper {
    public LotteryResponse toResponse(Lottery lotteryy) {
        if ( lotteryy == null ) {
            return null;
        }

        LotteryResponse lotteryResponse = new LotteryResponse();

        lotteryResponse.setId( lotteryy.getId() );
        lotteryResponse.setLotteryNumber( lotteryy.getLotteryNumber() );
        lotteryResponse.setTitle( lotteryy.getTitle() );
        lotteryResponse.setSubTitle( lotteryy.getSubTitle() );
        lotteryResponse.setDescription( lotteryy.getDescription() );
        lotteryResponse.setRegulation( lotteryy.getRegulation() );
        lotteryResponse.setBannerImageUrl( lotteryy.getBannerImageUrl() );
        lotteryResponse.setBannerPhoneImageUrl( lotteryy.getBannerPhoneImageUrl() );
        lotteryResponse.setThumbnailImageUrl( lotteryy.getThumbnailImageUrl() );
        lotteryResponse.setMaxQuantity( lotteryy.getMaxQuantity() );
        lotteryResponse.setMinQuantity( lotteryy.getMinQuantity() );
        lotteryResponse.setTicketValue( lotteryy.getTicketValue() );
        lotteryResponse.setStatus( lotteryy.getStatus() );
        lotteryResponse.setExecutionDate( lotteryy.getExecutionDate() );
        lotteryResponse.setCreatedDate( lotteryy.getCreatedDate() );
        lotteryResponse.setShowRanking( lotteryy.isShowRanking() );
        lotteryResponse.setPromotions( lotteryy.getPromotions());
        List<String> list = lotteryy.getPhotoLinks();
        if ( list != null ) {
            lotteryResponse.setPhotoLinks( new ArrayList<>( list ) );
        }
        List<String> list1 = lotteryy.getPhotoPhoneLinks();
        if ( list1 != null ) {
            lotteryResponse.setPhotoPhoneLinks( new ArrayList<>( list1 ) );
        }
        lotteryResponse.setTicketQuantity( lotteryy.getTicketQuantity() );
        lotteryResponse.setCheckoutTimeMinutes( lotteryy.getCheckoutTimeMinutes() );
        lotteryResponse.setWinnersQuantity( lotteryy.getWinnersQuantity() );

        return lotteryResponse;
    }


    public Lottery toEntity(CreateLotteryRequest createLotteryRequestt) {
        if ( createLotteryRequestt == null ) {
            return null;
        }

        Lottery lottery = new Lottery();

        lottery.setTitle( createLotteryRequestt.getTitle() );
        lottery.setSubTitle( createLotteryRequestt.getSubTitle() );
        lottery.setDescription( createLotteryRequestt.getDescription() );
        lottery.setRegulation( createLotteryRequestt.getRegulation() );
        lottery.setBannerImageUrl( createLotteryRequestt.getBannerImageUrl() );
        lottery.setBannerPhoneImageUrl( createLotteryRequestt.getBannerPhoneImageUrl() );
        lottery.setThumbnailImageUrl( createLotteryRequestt.getThumbnailImageUrl() );
        lottery.setMaxQuantity( createLotteryRequestt.getMaxQuantity() );
        lottery.setMinQuantity( createLotteryRequestt.getMinQuantity() );
        lottery.setTicketValue( createLotteryRequestt.getTicketValue() );
        lottery.setExecutionDate( createLotteryRequestt.getExecutionDate() );
        lottery.setShowRanking( createLotteryRequestt.isShowRanking() );
        List<String> list = createLotteryRequestt.getPhotoLinks();
        if ( list != null ) {
            lottery.setPhotoLinks( new ArrayList<>( list ) );
        }
        List<String> list1 = createLotteryRequestt.getPhotoPhoneLinks();
        if ( list1 != null ) {
            lottery.setPhotoPhoneLinks( new ArrayList<>( list1 ) );
        }
        lottery.setTicketQuantity( createLotteryRequestt.getTicketQuantity() );
        lottery.setCheckoutTimeMinutes( createLotteryRequestt.getCheckoutTimeMinutes() );
        lottery.setWinnersQuantity( createLotteryRequestt.getWinnersQuantity() );

        return lottery;
    }
}