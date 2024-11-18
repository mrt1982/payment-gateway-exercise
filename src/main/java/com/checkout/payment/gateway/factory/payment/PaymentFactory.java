package com.checkout.payment.gateway.factory.payment;

import com.checkout.payment.gateway.command.PaymentCommand;
import com.checkout.payment.gateway.factory.paymentmethod.PaymentMethodDetailsGenerator;
import com.checkout.payment.gateway.factory.paymentmethod.PaymentMethodDetailsGeneratorFactory;
import com.checkout.payment.gateway.model.PaymentStatus;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PaymentMethodDetails;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentFactory {
  private final PaymentMethodDetailsGeneratorFactory paymentMethodDetailsGeneratorFactory;

  public Payment createPayment(PaymentCommand processPaymentCommand, PaymentStatus status) {
    PaymentMethodDetailsGenerator generator =
        paymentMethodDetailsGeneratorFactory.getGenerator(processPaymentCommand.getPaymentMethodType());
    PaymentMethodDetails paymentMethodDetails = generator.generate(processPaymentCommand);

    return new Payment(
        processPaymentCommand.getIdempotencyKey(),
        status,
        processPaymentCommand.getCashAmount(),
        processPaymentCommand.getPaymentMethodType(),
        paymentMethodDetails
    );
  }
}
