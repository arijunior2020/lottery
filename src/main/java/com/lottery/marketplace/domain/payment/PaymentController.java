package com.lottery.marketplace.domain.payment;

import com.lottery.marketplace.domain.coupon.Coupon;
import com.lottery.marketplace.domain.coupon.CouponService;
import com.lottery.marketplace.domain.lottery.Lottery;
import com.lottery.marketplace.domain.lottery.LotteryService;
import com.lottery.marketplace.domain.payment.mercadopago.MercadoPagoPaymentImpl;
import com.lottery.marketplace.domain.ticket.Ticket;
import com.lottery.marketplace.domain.ticket.TicketService;
import com.lottery.marketplace.domain.ticket.TicketStatus;
import com.mercadopago.resources.payment.Payment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
@Tag(name = "Payments (pagamentos)")
public class PaymentController {

    private final LotteryService lotteryService;

    private final TicketService ticketService;

    private final List<PaymentStrategy> strategies;

    private final MercadoPagoPaymentImpl mercadoPagoPayment;

    private final CouponService couponService;

    @Operation(summary = "Realiza um pagamento.")
    @ApiResponses(value ={
      @ApiResponse(responseCode = "200", description = "Pagamento realizado com sucesso."),
      @ApiResponse(responseCode = "500", description = "Erro ao realizar pagamento.")
    })
    @PostMapping("/{lotteryId}/{email}")
    public ResponseEntity<PaymentResponse> createPayment(Authentication authentication,
                                              @PathVariable final String email,
                                              @PathVariable final String lotteryId,
                                              @RequestParam("amount") Long amount,
                                              @RequestParam("couponName") String couponName) {

        Lottery lottery = lotteryService.getLotteryById(UUID.fromString(lotteryId));

        Ticket ticket = lotteryService.buyTicket(lottery, email, amount);
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .paymentProviders(PaymentProviders.MERCADO_PAGO)
                .ticket(ticket)
                .build();

        PaymentStrategy paymentService = strategies.stream().filter(p -> p.acceptedBy(paymentRequest.getPaymentProviders()))
                .findFirst().orElseThrow(() -> new RuntimeException("Provider not found"));

        try {
            if (couponName != null) {
              Optional<Coupon> couponOptional = couponService.getCoupon(couponName);
              if (couponOptional.isPresent()) {
                couponService.validateCouponPublic(couponOptional.get());
                paymentService.createPayment(paymentRequest, couponOptional.get());
                if (couponOptional.get().getIsUnlimited() != null
                  && couponOptional.get().getIsUnlimited() == true) {
                  couponService.addAppliedQuantity(couponOptional.get());
                } else {
                  couponService.applyCouponChanges(couponOptional.get());
                }
              }
            }
            PaymentResponse payment = paymentService.createPayment(paymentRequest);
            ticket.setTransactionId(payment.getId());
            ticket.setQrCode(payment.getQrCode());
            ticket.setQrCodeBase64(payment.getQrCodeBase64());
            ticket.setQrCodeLink(payment.getQrCodeLink());
            ticketService.save(ticket);
            return ResponseEntity.ok(payment);
        } catch (Exception ex){
            log.error(ex.getMessage());
            ticket.setStatus(TicketStatus.ERROR);
            ticketService.save(ticket);
            throw ex;
        }
    }

    @Operation(summary = "Realiza busca de um pagamento por ID.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
      @ApiResponse(responseCode = "500", description = "Busca não permitida. Insira um ID válido.")
    })
    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPayment(Authentication authentication,
                                              @PathVariable final String paymentId) {
        return ResponseEntity.ok(mercadoPagoPayment.getPayment(Long.valueOf(paymentId)));
    }

    private void validateEmail(String email, Authentication authentication) {
        if (authentication != null && !email.equals(authentication.getName())) {
            throw new AccessDeniedException("Forbidden. You can only access your own info!");
        }
    }
}

