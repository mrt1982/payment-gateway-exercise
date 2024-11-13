package com.checkout.payment.rest.v1.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExpiryYearSizeValidator  implements
    ConstraintValidator<ValidExpiryYearSize, Integer> {

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext context) {
    if (value == null) {
      return true; // @NotNull will handle null check separately
    }
    String expiryYearStr = value.toString();
    int length = expiryYearStr.length();
    return length ==4;
  }
}
