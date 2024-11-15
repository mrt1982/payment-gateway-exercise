package com.checkout.payment.gateway.factory;

import com.checkout.payment.gateway.command.ProcessPaymentCommand;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PaymentMethodDetails;
import com.checkout.payment.gateway.model.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
//TODO: To be more polymorphic, abstract factory method. Base payment method type.
public class PaymentFactory {

  public Payment createPayment(ProcessPaymentCommand processPaymentCommand, PaymentStatus status) {
    int lastFourCardDigits = generateLastFourDigits(processPaymentCommand.getCardNumber());

    PaymentMethodDetails paymentMethodDetails = new PaymentMethodDetails(
        lastFourCardDigits,
        processPaymentCommand.getExpiryMonth(),
        processPaymentCommand.getExpiryYear()
    );

    return new Payment(
        processPaymentCommand.getIdempotencyKey(),
        status,
        processPaymentCommand.getCashAmount(),
        paymentMethodDetails
    );
  }

  private int generateLastFourDigits(long cardNumber){
    return (int) (cardNumber % 10000);
  }

}
