package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.command.PaymentCommand;
import com.checkout.payment.gateway.model.Payment;

public interface PaymentProcessor<T extends PaymentCommand> {
  Payment processPayment(T paymentCommand);
}
