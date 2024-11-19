package com.checkout.payment.gateway.factory.paymentmethod;

import com.checkout.payment.gateway.command.PaymentProcessCommand;
import com.checkout.payment.gateway.model.PaymentMethodDetails;

public interface PaymentMethodDetailsGenerator<T extends PaymentProcessCommand> {
  PaymentMethodDetails generate(T processPaymentCommand);
}
