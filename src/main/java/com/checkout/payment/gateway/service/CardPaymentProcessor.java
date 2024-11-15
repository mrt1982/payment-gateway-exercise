package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.command.CardProcessPaymentCommand;
import com.checkout.payment.gateway.factory.ProcessPaymentFactory;
import com.checkout.payment.gateway.model.PaymentStatus;
import com.checkout.payment.gateway.model.ProcessPayment;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CardPaymentProcessor implements PaymentProcessor<CardProcessPaymentCommand> {
  private final PaymentsRepository paymentsRepository;
  private final ProcessPaymentFactory processPaymentFactory;
  private final BankService bankService;

  @Override
  public ProcessPayment processPayment(CardProcessPaymentCommand paymentCommand) {
    PaymentStatus status = authoriseCardPayment(paymentCommand);
    ProcessPayment processPayment = processPaymentFactory.createPayment(paymentCommand, status);
    return processPayment;
  }

  private PaymentStatus authoriseCardPayment(CardProcessPaymentCommand paymentCommand) {
    return bankService.authorisePayment(paymentCommand.getCardNumber(), paymentCommand.getExpiryMonth(), paymentCommand.getExpiryYear(), paymentCommand.getCashAmount(), paymentCommand.getCvv());;
  }
}
