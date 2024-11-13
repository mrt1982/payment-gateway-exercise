package com.checkout.payment.gateway.factory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

import com.checkout.payment.gateway.command.ProcessPaymentCommand;
import com.checkout.payment.gateway.exception.ExpiredCardDateException;
import com.checkout.payment.gateway.model.CashAmount;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PaymentMethodDetails;
import com.checkout.payment.gateway.model.PaymentStatus;
import java.util.Currency;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PaymentFactorySmallTest {

  @Test
  void createPaymentFromProcessPaymentCommand_validProcessPaymentCommand_returnsPayment()
      throws ExpiredCardDateException {
    //Given
    PaymentFactory testObj = new PaymentFactory();
    UUID expectedIdempotencyKey = UUID.randomUUID();
    ProcessPaymentCommand processPaymentCommand = new ProcessPaymentCommand(expectedIdempotencyKey,
        new CashAmount(Currency.getInstance("GBP"), 150),
        123456789123456L,
        10,
        2025,
        500);
    PaymentMethodDetails expectedPaymentMethodDetails = new PaymentMethodDetails(3456, 10, 2025);
    //When
    Payment actualPayment = testObj.createPayment(processPaymentCommand, PaymentStatus.AUTHORIZED);
    //Then
    assertThat(actualPayment, allOf(
        hasProperty("idempotencyKey", is(equalTo(expectedIdempotencyKey))),
        hasProperty("status", is(equalTo(PaymentStatus.AUTHORIZED))),
        hasProperty("cashAmount", is(equalTo(new CashAmount(Currency.getInstance("GBP"), 150)))),
        hasProperty("paymentMethodDetails", is(equalTo(expectedPaymentMethodDetails)))));
  }
}