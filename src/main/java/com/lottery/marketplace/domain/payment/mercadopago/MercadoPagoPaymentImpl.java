package com.lottery.marketplace.domain.payment.mercadopago;

import com.lottery.marketplace.domain.coupon.Coupon;
import com.lottery.marketplace.domain.lottery.Lottery;
import com.lottery.marketplace.domain.lottery.LotteryService;
import com.lottery.marketplace.domain.payment.PaymentProviders;
import com.lottery.marketplace.domain.payment.PaymentRequest;
import com.lottery.marketplace.domain.payment.PaymentResponse;
import com.lottery.marketplace.domain.payment.PaymentStrategy;
import com.lottery.marketplace.domain.ticket.Ticket;
import com.lottery.marketplace.domain.ticket.TicketService;
import com.lottery.marketplace.domain.ticket.TicketStatus;
import com.lottery.marketplace.util.PhoneNumberExtractor;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.common.PhoneRequest;
import com.mercadopago.client.payment.*;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class MercadoPagoPaymentImpl implements PaymentStrategy {

    @Value("${payments.mercado-pago.access-token}")
    private String mercadoPagoAccessToken;

    @Value("${payments.mercado-pago.callback}")
    private String mercadoPagoCallback;

    private final TicketService ticketService;

    private final LotteryService lotteryService;

    public Payment getPayment(final Long paymentId){
        MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);

        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("x-idempotency-key", UUID.randomUUID().toString());

        MPRequestOptions requestOptions = MPRequestOptions.builder()
                .customHeaders(customHeaders)
                .build();

        PaymentClient client = new PaymentClient();

        try {
            return client.get(paymentId, requestOptions);
        } catch (MPException | MPApiException e) {
            throw new MercadoPagoException(e);
        }
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);
        ZoneId brazilZoneId = ZoneId.of("America/Sao_Paulo");

        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("x-idempotency-key", UUID.randomUUID().toString());

        MPRequestOptions requestOptions = MPRequestOptions.builder()
                .customHeaders(customHeaders)
                .build();

        PaymentClient client = new PaymentClient();

        PaymentCreateRequest paymentCreateRequest =
                PaymentCreateRequest.builder()
                        .additionalInfo(PaymentAdditionalInfoRequest.builder()
                                .payer(PaymentAdditionalInfoPayerRequest.builder()
                                        .firstName(paymentRequest.getTicket().getUserFirstName())
                                        .lastName(paymentRequest.getTicket().getUserLastName())
                                        .phone(PhoneRequest.builder()
                                                .areaCode(PhoneNumberExtractor.extractAreaCode(paymentRequest.getTicket().getUserPhone()))
                                                .number(PhoneNumberExtractor.extractNumberWithoutDash(paymentRequest.getTicket().getUserPhone()))
                                                .build()
                                        )
                                        .build()
                                )
                                .build())
                        .transactionAmount(paymentRequest.getTicket().getTicketValueTotal())
                        .externalReference(paymentRequest.getTicket().getId().toString())
                        .description("PC DOS SONHOS")
                        .paymentMethodId("pix")
                        .payer(PaymentPayerRequest.builder()
                                .entityType("individual")
                                .type("customer")
                                .email(paymentRequest.getTicket().getUserEmail())
                                .identification(IdentificationRequest.builder()
                                        .type("CPF")
                                        .number(paymentRequest.getTicket().getUserIdentification().replace(".", "").replace("-", ""))
                                        .build()
                                )
                                .build())
                        .notificationUrl(mercadoPagoCallback)
                        .dateOfExpiration(paymentRequest.getTicket().getExpirationDate().withZoneSameInstant(brazilZoneId).toOffsetDateTime())
                        .build();


        try {
            Payment payment = client.create(paymentCreateRequest, requestOptions);
            PaymentResponse paymentResponse = PaymentResponse.builder()
                    .id(payment.getId().toString())
                    .externalReference(payment.getExternalReference())
                    .status(payment.getStatus())
                    .qrCode(payment.getPointOfInteraction().getTransactionData().getQrCode())
                    .qrCodeBase64(payment.getPointOfInteraction().getTransactionData().getQrCodeBase64())
                    .qrCodeLink(payment.getPointOfInteraction().getTransactionData().getTicketUrl())
                    .expirationDate(payment.getDateOfExpiration())
                    .totalValue(payment.getTransactionAmount())
                    .build();
            log.info(payment.toString());
            return paymentResponse;
        } catch (MPException | MPApiException e) {
            throw new MercadoPagoException(e);
        }
    }

  @Override
  public PaymentResponse createPayment(PaymentRequest paymentRequest, Coupon coupon) {
    BigDecimal ticketValue = paymentRequest.getTicket().getTicketValueTotal();
    BigDecimal discountPercentage = BigDecimal.valueOf(coupon.getDiscountPercentage()).divide(BigDecimal.valueOf(100));
    BigDecimal discountValue = ticketValue.multiply(discountPercentage);
    BigDecimal discountedValue = ticketValue.subtract(discountValue);
    paymentRequest.getTicket().setTicketValueTotal(discountedValue);
    PaymentResponse paymentResponse = createPayment(paymentRequest);
    return paymentResponse;
  }


  public void confirmPayment(PaymentEvent paymentEvent){
        MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);

        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("x-idempotency-key", UUID.randomUUID().toString());

        MPRequestOptions requestOptions = MPRequestOptions.builder()
                .customHeaders(customHeaders)
                .build();

        try {
            PaymentClient client = new PaymentClient();
            log.info("Trying to get payment. dataId: {}", paymentEvent.getDataId());
            Payment payment = client.get(Long.valueOf(paymentEvent.getDataId()), requestOptions);

            if(PaymentStatus.APPROVED.equals(payment.getStatus())){
                Optional<Ticket> ticketOptional = ticketService.findById(UUID.fromString(payment.getExternalReference()));
                if(ticketOptional.isPresent()){
                    Ticket ticket = ticketOptional.get();
                    Lottery lottery = lotteryService.getLotteryById(ticket.getLotteryId());
                    if(TicketStatus.PENDING.equals(ticket.getStatus())){
                        ticket.setTicketNumber(ticketService.generateTicketNumber(ticket.getLotteryId(), ticket.getTicketAmount()));
                        checkLuckNumber(lottery, ticket);
                    }
                    ticket.setStatus(TicketStatus.PAYMENT_CONFIRMED);
                    ticketService.save(ticket);
                }
            }

            if(PaymentStatus.CANCELLED.equals(payment.getStatus())){
                Optional<Ticket> ticketOptional = ticketService.findById(UUID.fromString(payment.getExternalReference()));
                if(ticketOptional.isPresent()){
                    Ticket ticket = ticketOptional.get();
                    ticket.setTicketNumber(null);
                    ticket.setStatus(TicketStatus.EXPIRED);
                    ticketService.save(ticket);
                }
            }

        } catch (MPException | MPApiException e) {
            throw new MercadoPagoException(e);
        }

    }

    private static void checkLuckNumber(Lottery lottery, Ticket ticket) {
        List<String> listLuck = new ArrayList<>();
        if(null != lottery.getLuckNumber()){
            for(String luckNumber : lottery.getLuckNumber()){
                if(ticket.getTicketNumber().contains(luckNumber)){
                    listLuck.add(luckNumber);
                }
            }
            if(!listLuck.isEmpty()) {
                ticket.setLuckNumber(listLuck);
            }
        }
    }

    @Override
    public boolean acceptedBy(PaymentProviders paymentProviders) {
        return PaymentProviders.MERCADO_PAGO.equals(paymentProviders);
    }
}
