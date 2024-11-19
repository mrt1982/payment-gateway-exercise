package com.checkout.payment.gateway.command;

import com.checkout.payment.gateway.model.CashAmount;
import com.checkout.payment.gateway.model.PaymentMethodType;
import java.util.UUID;

public abstract class PaymentProcessCommand {
  protected final UUID idempotencyKey;
  protected final CashAmount cashAmount;
  protected final PaymentMethodType paymentMethodType;

  protected PaymentProcessCommand(UUID idempotencyKey, CashAmount cashAmount, PaymentMethodType paymentMethodType) {
    this.idempotencyKey = idempotencyKey;
    this.cashAmount = cashAmount;
    this.paymentMethodType = paymentMethodType;
  }

  public UUID getIdempotencyKey() {
    return idempotencyKey;
  }

  public CashAmount getCashAmount() {
    return cashAmount;
  }

  public PaymentMethodType getPaymentMethodType() {
    return paymentMethodType;
  }

}
