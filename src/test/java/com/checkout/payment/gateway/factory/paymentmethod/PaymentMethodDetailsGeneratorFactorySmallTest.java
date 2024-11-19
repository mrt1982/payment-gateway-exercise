package com.checkout.payment.gateway.factory.paymentmethod;

import com.checkout.payment.gateway.model.PaymentMethodType;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

class PaymentMethodDetailsGeneratorFactorySmallTest {

  @Test
  void getGenerator_cardPymentMethodType_returnPaymentMethodDetailsGenerator() {
    //Given
    PaymentMethodDetailsGeneratorFactory testObj = new PaymentMethodDetailsGeneratorFactory();
    //When
    PaymentMethodDetailsGenerator paymentMethodDetailsGenerator = testObj.getGenerator(PaymentMethodType.CARD);
    //Then
    assertThat(paymentMethodDetailsGenerator, instanceOf(CardPaymentMethodDetailsGenerator.class));
  }
}