package com.checkout.payment.gateway.command;

import com.checkout.payment.gateway.exception.ExpiredCardDateException;
import com.checkout.payment.gateway.model.CashAmount;
import java.time.YearMonth;
import java.util.UUID;
import lombok.Getter;

@Getter
/*
TODO: Make this class abstract. sub classes per payment method type.
 */
public class ProcessPaymentCommand {
  private final UUID idempotencyKey;
  private final CashAmount cashAmount;
  private final long cardNumber;
  private final int expiryMonth;
  private final int expiryYear;
  private final int cvv;

  public ProcessPaymentCommand(UUID idempotencyKey, CashAmount cashAmount,
      long cardNumber, int expiryMonth, int expiryYear, int cvv) throws ExpiredCardDateException {
    validateExpiryDate(expiryYear, expiryMonth);
    this.idempotencyKey = idempotencyKey;
    this.cashAmount = cashAmount;
    this.cardNumber = cardNumber;
    this.expiryMonth = expiryMonth;
    this.expiryYear = expiryYear;
    this.cvv = cvv;
  }

  private void validateExpiryDate(int expiryYear, int expiryMonth) throws ExpiredCardDateException {
    YearMonth yearMonth = YearMonth.of(expiryYear, expiryMonth);
    if (yearMonth.isBefore(YearMonth.now())) {
      throw new ExpiredCardDateException("Expiry date must be in the future");
    }
  }

}
