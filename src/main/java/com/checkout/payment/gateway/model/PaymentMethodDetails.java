package com.checkout.payment.gateway.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
//TODO: Refactor to be more polymorphic
public class PaymentMethodDetails {

  private final int lastFourCardDigits;
  private final int expiryMonth;
  private final int expiryYear;

  public PaymentMethodDetails(int lastFourCardDigits, int expiryMonth, int expiryYear) {
    this.lastFourCardDigits = lastFourCardDigits;
    this.expiryMonth = expiryMonth;
    this.expiryYear = expiryYear;
  }

  public int getLastFourCardDigits() {
    return this.lastFourCardDigits;
  }

  public int getExpiryMonth() {
    return this.expiryMonth;
  }

  public int getExpiryYear() {
    return this.expiryYear;
  }
}
