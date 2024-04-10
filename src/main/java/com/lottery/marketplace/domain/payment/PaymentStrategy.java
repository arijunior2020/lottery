package com.lottery.marketplace.domain.payment;

import com.lottery.marketplace.domain.coupon.Coupon;

public interface PaymentStrategy {

    PaymentResponse createPayment(PaymentRequest paymentRequest);

    PaymentResponse createPayment(PaymentRequest paymentRequest, Coupon coupon);

    boolean acceptedBy(PaymentProviders paymentProviders);

}
