package com.checkout.payment.gateway.factory.paymentprocessor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import com.checkout.payment.gateway.command.CardPaymentProcessCommand;
import com.checkout.payment.gateway.command.PaymentProcessCommand;
import com.checkout.payment.gateway.factory.payment.PaymentFactory;
import com.checkout.payment.gateway.model.PaymentMethodType;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.checkout.payment.gateway.service.BankService;
import com.checkout.payment.gateway.service.CardPaymentProcessor;
import com.checkout.payment.gateway.service.PaymentProcessor;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentProcessorFactorySmallTest {
  @Mock
  private PaymentsRepository paymentsRepositoryMock;

  @Mock
  private PaymentFactory paymentFactoryMock;

  @Mock
  private BankService bankServiceMock;

  @Test
  void getProcessor_cardPaymentMethodType_returnCardPaymentProcessor() {
    //Given
    PaymentProcessor<CardPaymentProcessCommand> cardPaymentProcessCommandPaymentProcessor = new CardPaymentProcessor(paymentsRepositoryMock, paymentFactoryMock, bankServiceMock);
    Map<PaymentMethodType, PaymentProcessor<? extends PaymentProcessCommand>> paymentProcessors = Map.of(PaymentMethodType.CARD,
        cardPaymentProcessCommandPaymentProcessor);
    PaymentProcessorFactory testObj = new PaymentProcessorFactory(paymentProcessors);
    //When
    PaymentProcessor<PaymentProcessCommand> actualPaymentProcessor = testObj.getProcessor(PaymentMethodType.CARD);
    //Then
    assertThat(actualPaymentProcessor, instanceOf(CardPaymentProcessor.class));
  }
}