package com.checkout.payment.rest.v1.assembly;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.checkout.payment.gateway.command.CardPaymentProcessCommand;
import com.checkout.payment.gateway.command.PaymentProcessCommand;
import com.checkout.payment.gateway.command.exception.ExpiredCardDateException;
import com.checkout.payment.gateway.model.CashAmount;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PaymentMethodType;
import com.checkout.payment.gateway.model.PaymentStatus;
import com.checkout.payment.gateway.model.card.CardPaymentMethodDetails;
import com.checkout.payment.rest.v1.request.CardPaymentRequest;
import com.checkout.payment.rest.v1.request.PaymentRequest;
import java.time.Year;
import java.util.Currency;
import java.util.UUID;
import com.checkout.payment.rest.v1.response.CardPaymentResponse;
import com.checkout.payment.rest.v1.response.PaymentResponse;
import org.junit.jupiter.api.Test;

class PaymentAssemblyTest {

  @Test
  void assemblePaymentRequestToPaymentProcessCommand_validCardPaymentRequest_returnCardPaymentProcessCommand()
      throws ExpiredCardDateException {
    //Given
    int expiryYear = Year.now().plusYears(1).getValue();
    UUID idempotencyKey = UUID.randomUUID();
    CashAmount cashAmount = new CashAmount(Currency.getInstance("GBP"), 150);
    PaymentRequest cardPaymentRequest = CardPaymentRequest.builder()
        .idempotencyKey(idempotencyKey.toString())
        .paymentMethodType("CARD")
        .cardNumber("123456789123456")
        .expiryMonth(10)
        .expiryYear(expiryYear)
        .currency(cashAmount.getCurrencyIso())
        .amount(String.valueOf(cashAmount.getMinorAmount()))
        .cvv("500")
        .build();
    //When
    PaymentProcessCommand actualPaymentProcessCommand = PaymentAssembly.assemblePaymentRequestToPaymentProcessCommand(cardPaymentRequest);
    //Then
    assertThat(actualPaymentProcessCommand, is(notNullValue()));
    assertThat(actualPaymentProcessCommand, instanceOf(CardPaymentProcessCommand.class));
    assertThat(actualPaymentProcessCommand.getIdempotencyKey(), is(equalTo(idempotencyKey)));
    assertThat(actualPaymentProcessCommand.getCashAmount(), is(equalTo(cashAmount)));
    assertThat(actualPaymentProcessCommand.getPaymentMethodType(), is(equalTo(PaymentMethodType.CARD)));
    CardPaymentProcessCommand actualCardPaymentProcessCommand = (CardPaymentProcessCommand)actualPaymentProcessCommand;
    assertThat(actualCardPaymentProcessCommand, allOf(
        hasProperty("cardNumber", is(equalTo(123456789123456L))),
        hasProperty("expiryMonth", is(equalTo(10))),
        hasProperty("expiryYear", is(equalTo(expiryYear))),
        hasProperty("cvv", is(equalTo(500)))));
  }

  @Test
  void assemblePaymentToPaymentResponse_validPaymentWithCardPaymentMethodDetails_returnCardPaymentResponse() {
    //Given
    UUID transactionId = UUID.randomUUID();
    UUID idempotencyKey = UUID.randomUUID();
    CashAmount cashAmount = new CashAmount(Currency.getInstance("GBP"), 150);
    CardPaymentMethodDetails cardPaymentMethodDetails = new CardPaymentMethodDetails(1234, 10, 2024);
    Payment cardPayment = new Payment(idempotencyKey, PaymentStatus.AUTHORIZED, cashAmount, PaymentMethodType.CARD, cardPaymentMethodDetails);
    cardPayment.setTransactionId(transactionId);
    //When
    PaymentResponse actualPaymentResponse = PaymentAssembly.assemblePaymentToPaymentResponse(cardPayment);
    //Then
    assertThat(actualPaymentResponse, is(notNullValue()));
    assertThat(actualPaymentResponse, instanceOf(CardPaymentResponse.class));
    CardPaymentResponse cardPaymentResponse = (CardPaymentResponse) actualPaymentResponse;
    assertThat(cardPaymentResponse, allOf(
        hasProperty("id", is(equalTo(transactionId))),
        hasProperty("idempotencyKey", is(equalTo(idempotencyKey))),
        hasProperty("status", is(equalTo(PaymentStatus.AUTHORIZED))),
        hasProperty("currency", is(equalTo(cashAmount.getCurrencyIso()))),
        hasProperty("amount", is(equalTo(cashAmount.getMinorAmount()))),
        hasProperty("paymentMethodType", is(equalTo(PaymentMethodType.CARD))),
        hasProperty("cardNumberLastFour", is(equalTo(1234))),
        hasProperty("expiryMonth", is(equalTo(10))),
        hasProperty("expiryYear", is(equalTo(2024)))));
  }
}