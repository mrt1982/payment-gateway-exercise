package com.checkout.payment.rest.v1.response;

import com.checkout.payment.gateway.model.PaymentMethodType;
import com.checkout.payment.gateway.model.PaymentStatus;
import java.util.UUID;

public class CardPaymentResponse extends PaymentResponse {
  private int cardNumberLastFour;
  private int expiryMonth;
  private int expiryYear;

  public CardPaymentResponse() {
  }

  public CardPaymentResponse(UUID id, UUID idempotencyKey, PaymentStatus status, String currency,
      int amount, PaymentMethodType paymentMethodType, int cardNumberLastFour, int expiryMonth, int expiryYear) {
    super(id, idempotencyKey, status, currency, amount, paymentMethodType);
    this.cardNumberLastFour = cardNumberLastFour;
    this.expiryMonth = expiryMonth;
    this.expiryYear = expiryYear;
  }

  public int getCardNumberLastFour() {
    return cardNumberLastFour;
  }

  public int getExpiryMonth() {
    return expiryMonth;
  }

  public int getExpiryYear() {
    return expiryYear;
  }

  @Override
  public String toString() {
    return "CardPaymentResponse{" +
        "id=" + id +
        ", idempotencyKey=" + idempotencyKey +
        ", status=" + status +
        ", cardNumberLastFour=" + cardNumberLastFour +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        ", paymentMethodType=" + paymentMethodType +
        '}';
  }
}
