package com.checkout.payment.gateway.factory.paymentprocessor;

import com.checkout.payment.gateway.command.PaymentCommand;
import com.checkout.payment.gateway.service.PaymentProcessor;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentProcessorFactory {
  private final Map<Class<? extends PaymentCommand>, PaymentProcessor<? extends PaymentCommand>> paymentProcessors;

  @SuppressWarnings("unchecked")
  public <T extends PaymentCommand> PaymentProcessor<T> getProcessor(Class<T> commandType) {
    return (PaymentProcessor<T>) paymentProcessors.get(commandType);
  }

}
