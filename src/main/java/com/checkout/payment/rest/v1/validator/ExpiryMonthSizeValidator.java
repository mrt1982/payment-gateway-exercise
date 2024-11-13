package com.checkout.payment.rest.v1.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExpiryMonthSizeValidator implements ConstraintValidator<ValidExpiryMonthSize, Integer> {

    @Override
    public boolean isValid(Integer expiryMonth, ConstraintValidatorContext context) {
      if (expiryMonth == null) {
        return true; // @NotNull will handle null check separately
      }
      return expiryMonth >= 1 && expiryMonth <= 12;
    }
  }

