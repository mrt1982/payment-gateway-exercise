package com.checkout.payment.gateway.factory.paymentmethod;

import com.checkout.payment.gateway.command.PaymentCommand;
import com.checkout.payment.gateway.model.PaymentMethodDetails;

public interface PaymentMethodDetailsGenerator<T extends PaymentCommand> {
  PaymentMethodDetails generate(T processPaymentCommand);
}
