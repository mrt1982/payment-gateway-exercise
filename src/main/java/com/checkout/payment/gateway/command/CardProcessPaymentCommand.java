package com.checkout.payment.gateway.command;

import com.checkout.payment.gateway.model.CashAmount;
import com.checkout.payment.gateway.model.PaymentMethodType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public class CardProcessPaymentCommand extends PaymentCommand {
  private final long cardNumber;
  private final int expiryMonth;
  private final int expiryYear;
  private final int cvv;

  public CardProcessPaymentCommand(UUID idempotencyKey, CashAmount cashAmount, PaymentMethodType paymentMethodType, long cardNumber,
      int expiryMonth, int expiryYear, int cvv) {
    super(idempotencyKey, cashAmount, paymentMethodType);
    this.cardNumber = cardNumber;
    this.expiryMonth = expiryMonth;
    this.expiryYear = expiryYear;
    this.cvv = cvv;
  }
}
