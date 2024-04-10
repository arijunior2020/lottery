package com.lottery.marketplace.domain.payment.mercadopago;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PaymentEvent {

    private String action;

    private String dataId;

    private String type;

    public PaymentEvent(Map<String, Object> messageContent){
        this.action = (String) messageContent.getOrDefault("action", "none");
        this.type = (String) messageContent.getOrDefault("type", "none");
        Map<String, Object> data = (Map<String, Object>) messageContent.getOrDefault("data", new HashMap<>());
        this.dataId = (String) data.getOrDefault("id", "none");
    }

}
