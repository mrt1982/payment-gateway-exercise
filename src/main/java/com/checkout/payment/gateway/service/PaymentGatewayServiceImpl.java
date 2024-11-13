package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.command.ProcessPaymentCommand;
import com.checkout.payment.gateway.factory.PaymentFactory;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PaymentStatus;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.Optional;
import java.util.UUID;
import com.checkout.payment.gateway.service.exception.PaymentAlreadyProcessedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

  private final PaymentsRepository paymentsRepository;
  private final PaymentFactory paymentFactory;
  private final BankService bankService;

  @Override
  public Optional<Payment> findPaymentsByTransactionId(UUID transactionId) {
    log.debug("Requesting access to to payment with TransactionId {}", transactionId);
    return paymentsRepository.getByTransactionId(transactionId);
  }

  @Override
  public Optional<Payment> findPaymentByIdempotencyId(UUID idempotencyKey) {
    return paymentsRepository.getByIdempotencyKey(idempotencyKey);
  }

  @Override
  public Payment processPayment(ProcessPaymentCommand command)
      throws PaymentAlreadyProcessedException {
    checkForIdempotency(command.getIdempotencyKey());
    PaymentStatus authorisationStatus = bankService.authorisePayment(command.getCardNumber(), command.getExpiryMonth(), command.getExpiryYear(), command.getCashAmount(), command.getCvv());
    Payment payment = paymentFactory.createPayment(command, authorisationStatus);
    return paymentsRepository.createPayment(payment);
  }

  private void checkForIdempotency(UUID idempotencyKey) throws PaymentAlreadyProcessedException {
    if (paymentsRepository.getByIdempotencyKey(idempotencyKey).isPresent()) {
      throw new PaymentAlreadyProcessedException("Payment has already been processed.");
    }
  }
}
