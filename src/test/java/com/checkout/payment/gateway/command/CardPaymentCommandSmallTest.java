package com.checkout.payment.gateway.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.checkout.payment.gateway.command.exception.ExpiredCardDateException;
import com.checkout.payment.gateway.model.CashAmount;
import java.util.Currency;
import java.util.UUID;
import com.checkout.payment.gateway.model.PaymentMethodType;
import org.junit.jupiter.api.Test;

class CardPaymentCommandSmallTest {

  @Test
  void createCardProcessPaymentCommand_valid_success() throws ExpiredCardDateException {
    //Given & When
    UUID expectedIdempotencyKey = UUID.randomUUID();
    System.out.println("UUID " + UUID.randomUUID());
    CashAmount expectedCashAmount = new CashAmount(Currency.getInstance("GBP"), 150);
    CardProcessPaymentCommand expectedCardProcessPaymentCommand = new CardProcessPaymentCommand(
        expectedIdempotencyKey,
        expectedCashAmount, PaymentMethodType.CARD, 12345L, 10, 2025, 500);
    //Then
    assertThat(expectedCardProcessPaymentCommand, allOf(
        hasProperty("idempotencyKey", is(equalTo(expectedIdempotencyKey))),
        hasProperty("cashAmount", is(equalTo(expectedCashAmount))),
        hasProperty("paymentMethodType", is(equalTo(PaymentMethodType.CARD))),
        hasProperty("cardNumber", is(equalTo(12345L))),
        hasProperty("expiryMonth", is(equalTo(10))),
        hasProperty("expiryYear", is(equalTo(2025))),
        hasProperty("cvv", is(equalTo(500)))));
  }

  @Test
  void createCardProcessPaymentCommand_cardExpiryDateIsInThePast_throwExpiredCardDateException()
  {
    //Given & When & Then
    UUID expectedIdempotencyKey = UUID.randomUUID();
    CashAmount expectedCashAmount = new CashAmount(Currency.getInstance("GBP"), 150);
    assertThrows(ExpiredCardDateException.class,
        () -> new CardProcessPaymentCommand(expectedIdempotencyKey,
            expectedCashAmount, PaymentMethodType.CARD, 12345L, 10, 2022, 500));
  }

}