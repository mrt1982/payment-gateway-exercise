package com.checkout.payment.gateway.exception;

public class ExpiredCardDateException extends Exception {

  public ExpiredCardDateException(String message) {
    super(message);
  }

}
