package com.lottery.marketplace.domain.ticket;

import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketResponse toResponse(Ticket ticket) {
        if ( ticket == null ) {
            return null;
        }

        TicketResponse ticketResponse = new TicketResponse();

        ticketResponse.setId( ticket.getId() );
        ticketResponse.setTicketNumber( ticket.getTicketNumber() );
        ticketResponse.setTicketValue( ticket.getTicketValue() );
        ticketResponse.setTicketAmount( ticket.getTicketAmount() );
        ticketResponse.setLotteryId( ticket.getLotteryId() );
        ticketResponse.setLotteryNumber( ticket.getLotteryNumber() );
        ticketResponse.setUserId( ticket.getUserId() );
        ticketResponse.setCreatedDate( ticket.getCreatedDate() );
        ticketResponse.setStatus( ticket.getStatus() );
        ticketResponse.setExpirationDate( ticket.getExpirationDate() );
        ticketResponse.setQrCode( ticket.getQrCode() );
        ticketResponse.setQrCodeBase64( ticket.getQrCodeBase64() );
        ticketResponse.setQrCodeLink( ticket.getQrCodeLink() );
        ticketResponse.setUserIdentification( ticket.getUserIdentification() );
        ticketResponse.setUserFirstName( ticket.getUserFirstName() );
        ticketResponse.setUserLastName( ticket.getUserLastName() );
        ticketResponse.setTicketValueTotal( ticket.getTicketValueTotal() );
        ticketResponse.setLuckNumber( ticket.getLuckNumber() );


        return ticketResponse;
    }
}