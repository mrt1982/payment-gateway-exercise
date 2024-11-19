package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.command.PaymentProcessCommand;
import com.checkout.payment.gateway.factory.paymentprocessor.PaymentProcessorFactory;
import com.checkout.payment.gateway.model.Payment;
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
/*
  TODO: Strategy pattern for the bank Service.
  PaymentProcessor base on payment method type.
  Leave idempotency check here as it uses for any payments for all payment method types
 */
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

  private final PaymentsRepository paymentsRepository;
  private final PaymentProcessorFactory paymentProcessorFactory;

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
  public Payment processPayment(PaymentProcessCommand paymentProcessCommand)
      throws PaymentAlreadyProcessedException {
    checkForIdempotency(paymentProcessCommand.getIdempotencyKey());
    PaymentProcessor<PaymentProcessCommand> paymentProcessor = getPaymentProcessor(
        paymentProcessCommand);
    return paymentProcessor.processPayment(paymentProcessCommand);
  }

  private void checkForIdempotency(UUID idempotencyKey) throws PaymentAlreadyProcessedException {
    if (paymentsRepository.getByIdempotencyKey(idempotencyKey).isPresent()) {
      throw new PaymentAlreadyProcessedException("Payment has already been processed.");
    }
  }

  PaymentProcessor<PaymentProcessCommand> getPaymentProcessor(
      PaymentProcessCommand paymentProcessCommand){
    PaymentProcessor<PaymentProcessCommand> processor = paymentProcessorFactory.getProcessor(
        paymentProcessCommand.getPaymentMethodType());
    if (processor == null) {
      throw new IllegalArgumentException("No processor found for payment method type: " + paymentProcessCommand.getPaymentMethodType().name());
    }
    return processor;
  }

}
