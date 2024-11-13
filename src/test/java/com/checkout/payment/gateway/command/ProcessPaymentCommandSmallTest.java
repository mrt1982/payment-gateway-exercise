package com.checkout.payment.gateway.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.checkout.payment.gateway.exception.ExpiredCardDateException;
import com.checkout.payment.gateway.model.CashAmount;
import java.util.Currency;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProcessPaymentCommandSmallTest {

  @Test
  void createProcessPaymentCommand_valid_success() throws ExpiredCardDateException {
    //Given & When
    UUID expectedIdempotencyKey = UUID.randomUUID();
    System.out.println("UUID " + UUID.randomUUID());
    CashAmount expectedCashAmount = new CashAmount(Currency.getInstance("GBP"), 150);
    ProcessPaymentCommand expectedProcessPaymentCommand = new ProcessPaymentCommand(
        expectedIdempotencyKey,
        expectedCashAmount, 12345L, 10, 2025, 500);
    //Then
    assertThat(expectedProcessPaymentCommand, allOf(
        hasProperty("idempotencyKey", is(equalTo(expectedIdempotencyKey))),
        hasProperty("cashAmount", is(equalTo(expectedCashAmount))),
        hasProperty("cardNumber", is(equalTo(12345L))),
        hasProperty("expiryMonth", is(equalTo(10))),
        hasProperty("expiryYear", is(equalTo(2025))),
        hasProperty("cvv", is(equalTo(500)))));
  }

  @Test
  void createProcessPaymentCommand_cardExpiryDateIsInThePast_throwExpiredCardDateException()
  {
    //Given & When & Then
    UUID expectedIdempotencyKey = UUID.randomUUID();
    CashAmount expectedCashAmount = new CashAmount(Currency.getInstance("GBP"), 150);
    assertThrows(ExpiredCardDateException.class,
        () -> new ProcessPaymentCommand(expectedIdempotencyKey,
            expectedCashAmount, 12345L, 10, 2022, 500));
  }

}