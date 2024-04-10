package com.lottery.marketplace.domain.payment.mercadopago;

public class MercadoPagoException extends RuntimeException{

    MercadoPagoException(Throwable throwable){
        super(throwable);
    }
}
