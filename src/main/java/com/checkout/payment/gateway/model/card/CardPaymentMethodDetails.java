package com.checkout.payment.gateway.model.card;

import com.checkout.payment.gateway.model.PaymentMethodDetails;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class CardPaymentMethodDetails implements PaymentMethodDetails {
  private final int lastFourCardDigits;
  private final int expiryMonth;
  private final int expiryYear;

  public CardPaymentMethodDetails(int lastFourCardDigits, int expiryMonth, int expiryYear) {
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
