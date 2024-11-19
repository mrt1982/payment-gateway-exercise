package com.checkout.payment.gateway.factory.payment;

import com.checkout.payment.gateway.command.PaymentProcessCommand;
import com.checkout.payment.gateway.factory.paymentmethod.PaymentMethodDetailsGenerator;
import com.checkout.payment.gateway.factory.paymentmethod.PaymentMethodDetailsGeneratorFactory;
import com.checkout.payment.gateway.model.PaymentStatus;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PaymentMethodDetails;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentFactory {
  private final PaymentMethodDetailsGeneratorFactory paymentMethodDetailsGeneratorFactory;

  public Payment createPayment(PaymentProcessCommand processPaymentProcessCommand, PaymentStatus status) {
    PaymentMethodDetailsGenerator generator =
        paymentMethodDetailsGeneratorFactory.getGenerator(processPaymentProcessCommand.getPaymentMethodType());
    PaymentMethodDetails paymentMethodDetails = generator.generate(processPaymentProcessCommand);

    return new Payment(
        processPaymentProcessCommand.getIdempotencyKey(),
        status,
        processPaymentProcessCommand.getCashAmount(),
        processPaymentProcessCommand.getPaymentMethodType(),
        paymentMethodDetails
    );
  }
}
