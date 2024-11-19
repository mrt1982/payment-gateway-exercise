package com.checkout.payment.gateway.command.exception;

public class ExpiredCardDateException extends Exception {

  public ExpiredCardDateException(String message) {
    super(message);
  }

}
