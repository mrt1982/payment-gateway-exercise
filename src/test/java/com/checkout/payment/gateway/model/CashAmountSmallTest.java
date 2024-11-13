package com.checkout.payment.gateway.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;
import java.util.Currency;
import org.junit.jupiter.api.Test;

class CashAmountSmallTest {

  @Test
  void getMajorAmount_currencyWithZeroDecimalPlace_returnsMajorAmountWithZeroDecimalPlace() {
    //Given
    CashAmount testObj = new CashAmount(Currency.getInstance("JPY"), 150);
    //When
    BigDecimal actualMajorAmount = testObj.getMajorAmount();
    //Then
    assertThat(actualMajorAmount.compareTo(new BigDecimal("150")), is(0));
  }

  @Test
  void getMajorAmount_currencyWithTwoDecimalPlace_returnsMajorAmountWithTwoDecimalPlace() {
    //Given
    CashAmount testObj = new CashAmount(Currency.getInstance("GBP"), 150);
    //When
    BigDecimal actualMajorAmount = testObj.getMajorAmount();
    //Then
    assertThat(actualMajorAmount.compareTo(new BigDecimal("1.50")), is(0));
  }

  @Test
  void getMajorAmount_currencyWithThreeDecimalPlace_returnsMajorAmountWithThreeDecimalPlace() {
    //Given
    CashAmount testObj = new CashAmount(Currency.getInstance("BHD"), 1500);
    //When
    BigDecimal actualMajorAmount = testObj.getMajorAmount();
    //Then
    assertThat(actualMajorAmount.compareTo(new BigDecimal("1.500")), is(0));
  }

  @Test
  void getMinorAmount_validCurrencyAmount_returnsExactMinorAmount() {
    //Given
    CashAmount testObj = new CashAmount(Currency.getInstance("GBP"), 150);
    //When
    int actualMinorAmount = testObj.getMinorAmount();
    //Then
    assertThat(actualMinorAmount, is(equalTo(150)));
  }
}