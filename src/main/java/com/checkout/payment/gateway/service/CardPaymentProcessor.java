package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.command.CardProcessPaymentCommand;
import com.checkout.payment.gateway.factory.payment.PaymentFactory;
import com.checkout.payment.gateway.model.PaymentStatus;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CardPaymentProcessor implements PaymentProcessor<CardProcessPaymentCommand> {
  private final PaymentsRepository paymentsRepository;
  private final PaymentFactory paymentFactory;
  private final BankService bankService;

  @Override
  public Payment processPayment(CardProcessPaymentCommand paymentCommand) {
    PaymentStatus status = authoriseCardPayment(paymentCommand);
    Payment payment = paymentFactory.createPayment(paymentCommand, status);
    return paymentsRepository.createPayment(payment);
  }

  private PaymentStatus authoriseCardPayment(CardProcessPaymentCommand paymentCommand) {
    return bankService.authorisePayment(paymentCommand.getCardNumber(), paymentCommand.getExpiryMonth(), paymentCommand.getExpiryYear(), paymentCommand.getCashAmount(), paymentCommand.getCvv());
  }
}
