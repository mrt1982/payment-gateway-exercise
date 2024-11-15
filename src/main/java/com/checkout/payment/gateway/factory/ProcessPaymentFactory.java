package com.checkout.payment.gateway.factory;

import com.checkout.payment.gateway.command.PaymentCommand;
import com.checkout.payment.gateway.factory.paymentmethod.PaymentMethodDetailsGenerator;
import com.checkout.payment.gateway.factory.paymentmethod.PaymentMethodDetailsGeneratorFactory;
import com.checkout.payment.gateway.model.PaymentStatus;
import com.checkout.payment.gateway.model.ProcessPayment;
import com.checkout.payment.gateway.model.ProcessPaymentMethodDetails;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProcessPaymentFactory {
  private final PaymentMethodDetailsGeneratorFactory paymentMethodDetailsGeneratorFactory;

  public ProcessPayment createPayment(PaymentCommand processPaymentCommand, PaymentStatus status) {
    PaymentMethodDetailsGenerator generator =
        paymentMethodDetailsGeneratorFactory.getGenerator(processPaymentCommand.getPaymentMethodType());
    ProcessPaymentMethodDetails processPaymentMethodDetails = generator.generate(processPaymentCommand);

    return new ProcessPayment(
        processPaymentCommand.getIdempotencyKey(),
        status,
        processPaymentCommand.getCashAmount(),
        processPaymentCommand.getPaymentMethodType(),
        processPaymentMethodDetails
    );
  }
}
