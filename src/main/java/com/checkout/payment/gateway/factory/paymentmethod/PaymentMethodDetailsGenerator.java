package com.checkout.payment.gateway.factory.paymentmethod;

import com.checkout.payment.gateway.command.PaymentCommand;
import com.checkout.payment.gateway.model.ProcessPaymentMethodDetails;

public interface PaymentMethodDetailsGenerator {
  ProcessPaymentMethodDetails generate(PaymentCommand processPaymentCommand);
}
