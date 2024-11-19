package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.command.PaymentProcessCommand;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.service.exception.PaymentAlreadyProcessedException;
import java.util.Optional;
import java.util.UUID;

public interface PaymentGatewayService {
  Optional<Payment> findPaymentsByTransactionId(UUID transactionId);
  Optional<Payment> findPaymentByIdempotencyId(UUID idempotencyKey);
  Payment processPayment(PaymentProcessCommand command) throws PaymentAlreadyProcessedException;
}
