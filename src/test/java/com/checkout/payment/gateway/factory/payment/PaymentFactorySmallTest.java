package com.checkout.payment.gateway.factory.payment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

import com.checkout.payment.gateway.command.CardPaymentProcessCommand;
import com.checkout.payment.gateway.command.exception.ExpiredCardDateException;
import com.checkout.payment.gateway.factory.paymentmethod.CardPaymentMethodDetailsGenerator;
import com.checkout.payment.gateway.factory.paymentmethod.PaymentMethodDetailsGeneratorFactory;
import com.checkout.payment.gateway.model.CashAmount;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PaymentMethodDetails;
import com.checkout.payment.gateway.model.PaymentMethodType;
import com.checkout.payment.gateway.model.PaymentStatus;
import com.checkout.payment.gateway.model.card.CardPaymentMethodDetails;
import java.time.Year;
import java.util.Currency;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentFactorySmallTest {
  @Mock
  private CardPaymentMethodDetailsGenerator cardPaymentMethodDetailsGeneratorMock;
  @Mock
  private PaymentMethodDetailsGeneratorFactory paymentMethodDetailsGeneratorFactoryMock;

  @Test
  void createCardPayment() throws ExpiredCardDateException {
    //Given
    int expiryYear = Year.now().plusYears(1).getValue();
    UUID idempotencyKey = UUID.randomUUID();
    CashAmount cashAmount = new CashAmount(Currency.getInstance("GBP"), 150);
    CardPaymentProcessCommand cardPaymentProcessCommand = new CardPaymentProcessCommand(
        idempotencyKey, cashAmount, PaymentMethodType.CARD, 123456789123456L,10,
        expiryYear, 500);
    PaymentMethodDetails cardPaymentMethodDetails = new CardPaymentMethodDetails(3456, 10, expiryYear);
    when(cardPaymentMethodDetailsGeneratorMock.generate(cardPaymentProcessCommand)).thenReturn(cardPaymentMethodDetails);
    when(paymentMethodDetailsGeneratorFactoryMock.getGenerator(PaymentMethodType.CARD)).thenReturn(
        cardPaymentMethodDetailsGeneratorMock);
    PaymentFactory testObj = new PaymentFactory(paymentMethodDetailsGeneratorFactoryMock);
    //When
    Payment actualCardPayment = testObj.createPayment(cardPaymentProcessCommand, PaymentStatus.AUTHORIZED);
    //Then
    assertThat(actualCardPayment, is(notNullValue()));
    assertThat(actualCardPayment.getIdempotencyKey(), is(equalTo(idempotencyKey)));
    assertThat(actualCardPayment.getStatus(), is(equalTo(PaymentStatus.AUTHORIZED)));
    assertThat(actualCardPayment.getCashAmount(), is(equalTo(cashAmount)));
    assertThat(actualCardPayment.getPaymentMethodType(), is(equalTo(PaymentMethodType.CARD)));
    assertThat(actualCardPayment.getPaymentMethodDetails(), is(equalTo(cardPaymentMethodDetails)));
  }
}