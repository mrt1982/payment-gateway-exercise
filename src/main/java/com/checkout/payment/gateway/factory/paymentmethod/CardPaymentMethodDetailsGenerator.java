package com.checkout.payment.gateway.factory.paymentmethod;

import com.checkout.payment.gateway.command.CardProcessPaymentCommand;
import com.checkout.payment.gateway.model.card.CardPaymentMethodDetails;
import com.checkout.payment.gateway.model.PaymentMethodDetails;

public class CardPaymentMethodDetailsGenerator implements PaymentMethodDetailsGenerator<CardProcessPaymentCommand> {

  @Override
  public PaymentMethodDetails generate(CardProcessPaymentCommand cardProcessPaymentCommand) {
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
