package com.checkout.payment.infrastructure.persistance;

import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.springframework.stereotype.Repository;

/**
 * A simple in-memory implementation of the {@link PaymentsRepository} interface that is
 * thread-safe.
 * <p>
 * This repository stores payments in two {@link ConcurrentHashMap} instances:
 * <ul>
 *     <li>One map stores payments by their transaction ID</li>
 *     <li>Another map stores payments by their idempotency key</li>
 * </ul>
 *
 * <p>Thread safety is achieved as follows:</p>
 * <ul>
 *     <li>Concurrent reads are supported by using {@link ConcurrentHashMap}, which ensures thread-safe access to the maps during read operations.</li>
 *     <li>For write operations, a {@link ReentrantReadWriteLock} is used to ensure that updates to both maps (transaction ID and idempotency key) are atomic and synchronized.</li>
 * </ul>
 *
 * <p>Note that the write lock is only used for the creation of payments, ensuring that the payment is added atomically to both maps. Read operations such as {@link #getByTransactionId(UUID)} and {@link #getByIdempotencyKey(UUID)} are lock-free as {@link ConcurrentHashMap} handles thread safety for these operations.</p>
 *
 * @see PaymentsRepository
 * @see ConcurrentHashMap
 * @see ReentrantReadWriteLock
 */
@Repository
public class SimpleInMemoryPaymentsRepository implements PaymentsRepository {

  private final Map<UUID, Payment> payments = new ConcurrentHashMap<>();
  private final Map<UUID, UUID> paymentsIndexByIdempotencyKey = new ConcurrentHashMap<>();
  private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

  @Override
  public Optional<Payment> getByTransactionId(UUID transactionId) {
    return Optional.ofNullable(payments.get(transactionId));
  }

  @Override
  public Optional<Payment> getByIdempotencyKey(UUID idempotencyKey) {
    Optional<UUID> paymentIdOpt = Optional.ofNullable(paymentsIndexByIdempotencyKey.get(idempotencyKey));
    return paymentIdOpt.map(payments::get);
  }

  @Override
  public Payment createPayment(Payment payment) {
    readWriteLock.writeLock().lock();
    try {
      UUID paymentTransactionId = generateTransactionId();
      payment.setTransactionId(paymentTransactionId);
      payments.put(payment.getTransactionId(), payment);
      paymentsIndexByIdempotencyKey.put(payment.getIdempotencyKey(), paymentTransactionId);
      return payment;
    } finally {
      readWriteLock.writeLock().unlock();
    }
  }

  private UUID generateTransactionId() {
    return UUID.randomUUID();
  }

}
