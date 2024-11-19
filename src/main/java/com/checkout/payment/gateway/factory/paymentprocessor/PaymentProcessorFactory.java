package com.checkout.payment.gateway.factory.paymentprocessor;

import com.checkout.payment.gateway.command.PaymentProcessCommand;
import com.checkout.payment.gateway.model.PaymentMethodType;
import com.checkout.payment.gateway.service.PaymentProcessor;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentProcessorFactory {
  private final Map<PaymentMethodType, PaymentProcessor<? extends PaymentProcessCommand>> paymentProcessors;

  @SuppressWarnings("unchecked")
  public <T extends PaymentProcessCommand> PaymentProcessor<T> getProcessor(PaymentMethodType paymentMethodType) {
    return (PaymentProcessor<T>) paymentProcessors.get(paymentMethodType);
  }

}
