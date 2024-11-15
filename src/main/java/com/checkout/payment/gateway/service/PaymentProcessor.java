package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.command.PaymentCommand;
import com.checkout.payment.gateway.model.ProcessPayment;

public interface PaymentProcessor<T extends PaymentCommand> {
  ProcessPayment processPayment(T paymentCommand);
}
