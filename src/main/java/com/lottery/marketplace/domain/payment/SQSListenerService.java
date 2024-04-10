package com.lottery.marketplace.domain.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lottery.marketplace.domain.payment.mercadopago.MercadoPagoPaymentImpl;
import com.lottery.marketplace.domain.payment.mercadopago.PaymentEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!local")
public class SQSListenerService {

    private final ObjectMapper objectMapper;

    private final MercadoPagoPaymentImpl mercadoPagoPayment;

    @SqsListener(value = "${messaging.sqs.payment-status.url}", messageVisibilitySeconds = "30", maxMessagesPerPoll = "1", maxConcurrentMessages = "1")
    public void getMessageFromSQS(String messageBody) throws JsonProcessingException {
        log.info("Receiving payment message.");
        Map<String, Object> notificationMap = objectMapper.readValue(messageBody, new TypeReference<Map<String, Object>>(){});

        log.info("SNS Event converted.");
        String messageContent = (String) notificationMap.get("Message");

        log.info("Message converted to string. {}", messageContent);
        if(StringUtils.contains(messageContent, "action")){
            Map<String, Object> messageMap = objectMapper.readValue(messageContent, new TypeReference<Map<String, Object>>(){});
            PaymentEvent paymentEvent = new PaymentEvent(messageMap);

            log.info("Succesfully converted. {}", paymentEvent);
            if(StringUtils.equals(paymentEvent.getAction(), "payment.updated") && StringUtils.equals(paymentEvent.getType(), "payment") ){
                mercadoPagoPayment.confirmPayment(paymentEvent);
            }
        } else {
            log.info("Not valuable message. {}", messageContent);
        }
    }

}