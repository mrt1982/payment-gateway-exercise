package com.checkout.payment.gateway.service.exception;

public class PaymentAlreadyProcessedException extends Exception {

  public PaymentAlreadyProcessedException(String message) {
    super(message);
  }
}
