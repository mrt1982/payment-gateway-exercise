package com.checkout.payment.gateway.factory.paymentmethod;

import com.checkout.payment.gateway.model.PaymentMethodType;

public class PaymentMethodDetailsGeneratorFactory {
  public PaymentMethodDetailsGenerator getGenerator(PaymentMethodType type) {
    return switch (type) {
      case CARD -> new CardPaymentMethodDetailsGenerator();
    };
  }
}
