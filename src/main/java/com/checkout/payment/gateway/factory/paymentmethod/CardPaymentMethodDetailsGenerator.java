package com.checkout.payment.gateway.factory.paymentmethod;

import com.checkout.payment.gateway.command.CardPaymentProcessCommand;
import com.checkout.payment.gateway.model.card.CardPaymentMethodDetails;
import com.checkout.payment.gateway.model.PaymentMethodDetails;

public class CardPaymentMethodDetailsGenerator implements PaymentMethodDetailsGenerator<CardPaymentProcessCommand> {

  @Override
  public PaymentMethodDetails generate(CardPaymentProcessCommand cardProcessPaymentCommand) {
    int lastFourDigits = generateLastFourDigits(cardProcessPaymentCommand.getCardNumber());
    return new CardPaymentMethodDetails(
        lastFourDigits,
        cardProcessPaymentCommand.getExpiryMonth(),
        cardProcessPaymentCommand.getExpiryYear()
    );
  }
  private int generateLastFourDigits(long cardNumber){
    return (int) (cardNumber % 10000);
  }
}
