package com.checkout.payment.rest.v1.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Currency;

public class CurrencyIsoValidator implements ConstraintValidator<ValidCurrencyIso, String> {

  @Override
  public boolean isValid(String currencyIso, ConstraintValidatorContext context) {
    if (currencyIso == null) {
      return true; // @NotNull will handle null check separately
    }
    try{
      Currency.getInstance(currencyIso);
    }catch (IllegalArgumentException | NullPointerException ex){
      return false;
    }
   return true;
  }
}
