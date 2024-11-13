package com.checkout.payment.gateway.model;

import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

@EqualsAndHashCode
public class CashAmount {
  private final Currency currency;
  private final BigDecimal scaledMajorAmount;

  public CashAmount(Currency currency, int unscaledMinorAmountValue){
    this.currency = currency;
    int currencyDecimalPlaces = currency.getDefaultFractionDigits();
    //To prevent Non-Terminating Decimals by using RoundingMode
    this.scaledMajorAmount = BigDecimal.valueOf(unscaledMinorAmountValue, currencyDecimalPlaces)
        .setScale(currencyDecimalPlaces, RoundingMode.HALF_UP);
  }

  public BigDecimal getMajorAmount(){
    return scaledMajorAmount;
  }

  public int getMinorAmount(){
    return scaledMajorAmount.movePointRight(currency.getDefaultFractionDigits()).intValueExact();
  }

  public String getCurrencyIso(){
    return currency.getCurrencyCode();
  }

  @Override
  public String toString() {
    return currency.getSymbol() + " " + getMajorAmount().toPlainString();
  }

}
