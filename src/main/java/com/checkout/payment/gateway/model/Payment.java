package com.checkout.payment.gateway.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import java.util.UUID;


@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Payment {
  private UUID transactionId;
  private final UUID idempotencyKey;
  private final PaymentStatus status;
  private final CashAmount cashAmount;
  private final PaymentMethodDetails paymentMethodDetails;

  public void setTransactionId(UUID transactionId){
    this.transactionId = transactionId;
  }
}
