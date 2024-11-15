package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.command.PaymentCommand;
import com.checkout.payment.gateway.command.ProcessPaymentCommand;
import com.checkout.payment.gateway.factory.PaymentFactory;
import com.checkout.payment.gateway.factory.paymentprocessor.PaymentProcessorFactory;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PaymentStatus;
import com.checkout.payment.gateway.model.ProcessPayment;
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
  private final PaymentProcessorFactory paymentProcessorFactory;
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
  public ProcessPayment processPayment(PaymentCommand paymentCommand)
      throws PaymentAlreadyProcessedException {
    checkForIdempotency(paymentCommand.getIdempotencyKey());
    PaymentProcessor<PaymentCommand> paymentProcessor = getPaymentProcessor(paymentCommand);
    return paymentProcessor.processPayment(paymentCommand);
  }

  private void checkForIdempotency(UUID idempotencyKey) throws PaymentAlreadyProcessedException {
    if (paymentsRepository.getByIdempotencyKey(idempotencyKey).isPresent()) {
      throw new PaymentAlreadyProcessedException("Payment has already been processed.");
    }
  }

  PaymentProcessor<PaymentCommand> getPaymentProcessor(PaymentCommand paymentCommand){
    @SuppressWarnings("unchecked")
    PaymentProcessor<PaymentCommand> processor = (PaymentProcessor<PaymentCommand>) paymentProcessorFactory.getProcessor(paymentCommand.getClass());
    if (processor == null) {
      throw new IllegalArgumentException("No processor found for command type: " + paymentCommand.getClass().getName());
    }
    return processor;
  }

}
