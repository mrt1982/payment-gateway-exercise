package com.checkout.payment.gateway.factory.paymentmethod;

import com.checkout.payment.gateway.command.CardPaymentProcessCommand;
import com.checkout.payment.gateway.command.exception.ExpiredCardDateException;
import com.checkout.payment.gateway.model.CashAmount;
import com.checkout.payment.gateway.model.PaymentMethodType;
import com.checkout.payment.gateway.model.card.CardPaymentMethodDetails;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.Currency;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class CardPaymentMethodDetailsGeneratorSmallTest {

  @Test
  void generate_validCardProcessPaymentCommand_returnCardPaymentMethodDetailsReturnLast4DigitsCardNumber()
      throws ExpiredCardDateException {
    //Given
    CardPaymentMethodDetailsGenerator testObj = new CardPaymentMethodDetailsGenerator();
    UUID idempotencyKey = UUID.randomUUID();
    CashAmount cashAmount = new CashAmount(Currency.getInstance("GBP"), 150);
    long cardNumber = 123456789102345L;
    int expiryMonth = 10;
    int expiryYear = Year.now().plusYears(1).getValue();
    int cvv = 500;
    CardPaymentProcessCommand cardProcessPaymentCommand = new CardPaymentProcessCommand(idempotencyKey,cashAmount, PaymentMethodType.CARD, cardNumber, expiryMonth, expiryYear, cvv);
    //When
    CardPaymentMethodDetails actualCardPaymentMethodDetails = (CardPaymentMethodDetails) testObj.generate(cardProcessPaymentCommand);
    //Then
    assertThat(actualCardPaymentMethodDetails, is(notNullValue()));
    assertThat(actualCardPaymentMethodDetails, allOf(
        hasProperty("lastFourCardDigits", is(equalTo(2345))),
        hasProperty("expiryMonth", is(equalTo(expiryMonth))),
        hasProperty("expiryYear", is(equalTo(expiryYear)))));
  }
}