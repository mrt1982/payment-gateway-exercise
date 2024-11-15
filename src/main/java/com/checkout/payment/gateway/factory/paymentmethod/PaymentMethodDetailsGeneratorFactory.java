package com.checkout.payment.gateway.factory.paymentmethod;

import com.checkout.payment.gateway.model.PaymentMethodType;

public class PaymentMethodDetailsGeneratorFactory {
  public PaymentMethodDetailsGenerator getGenerator(PaymentMethodType type) {
    switch (type) {
      case CARD:
        return new CardPaymentMethodDetailsGenerator();
      default:
        throw new UnsupportedOperationException("Payment method type not supported: " + type);
    }
  }

}
