package com.checkout.payment.gateway.factory.paymentmethod;

import com.checkout.payment.gateway.command.CardProcessPaymentCommand;
import com.checkout.payment.gateway.command.PaymentCommand;
import com.checkout.payment.gateway.model.CardPaymentMethodDetails;
import com.checkout.payment.gateway.model.ProcessPaymentMethodDetails;

public class CardPaymentMethodDetailsGenerator implements PaymentMethodDetailsGenerator {

  @Override
  public ProcessPaymentMethodDetails generate(PaymentCommand processPaymentCommand) {
    if (!(processPaymentCommand instanceof CardProcessPaymentCommand)) {
      throw new IllegalArgumentException("Invalid payment command for card payment");
    }

    CardProcessPaymentCommand cardCommand = (CardProcessPaymentCommand) processPaymentCommand;
    int lastFourDigits = generateLastFourDigits(cardCommand.getCardNumber());

    return new CardPaymentMethodDetails(
        lastFourDigits,
        cardCommand.getExpiryMonth(),
        cardCommand.getExpiryYear()
    );
  }
  private int generateLastFourDigits(long cardNumber){
    return (int) (cardNumber % 10000);
  }
}
