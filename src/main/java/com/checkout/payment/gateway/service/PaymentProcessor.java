package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.command.PaymentProcessCommand;
import com.checkout.payment.gateway.model.Payment;

public interface PaymentProcessor<T extends PaymentProcessCommand> {
  Payment processPayment(T paymentCommand);
}
