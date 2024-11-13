package com.checkout.payment.gateway.repository;

import com.checkout.payment.gateway.model.Payment;
import java.util.Optional;
import java.util.UUID;

public interface PaymentsRepository {
  Optional<Payment> getByTransactionId(UUID transactionId);
  Optional<Payment> getByIdempotencyKey(UUID idempotencyKey);
  Payment createPayment(Payment payment);
}
