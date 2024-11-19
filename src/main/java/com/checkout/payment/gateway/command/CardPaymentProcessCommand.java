package com.checkout.payment.gateway.command;

import com.checkout.payment.gateway.command.exception.ExpiredCardDateException;
import com.checkout.payment.gateway.model.CashAmount;
import com.checkout.payment.gateway.model.PaymentMethodType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.time.YearMonth;
import java.util.UUID;

@Getter
@EqualsAndHashCode(callSuper = true)
public class CardPaymentProcessCommand extends PaymentProcessCommand {
  private final long cardNumber;
  private final int expiryMonth;
  private final int expiryYear;
  private final int cvv;

  public CardPaymentProcessCommand(UUID idempotencyKey, CashAmount cashAmount, PaymentMethodType paymentMethodType, long cardNumber,
                                          int expiryMonth, int expiryYear, int cvv) throws ExpiredCardDateException {
    super(idempotencyKey, cashAmount, paymentMethodType);
    validateExpiryDate(expiryYear, expiryMonth);
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
