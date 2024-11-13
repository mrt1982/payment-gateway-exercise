package com.checkout.payment.rest.v1.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CurrencyIsoValidator.class)
public @interface ValidCurrencyIso {
  String message() default "Currency must be a valid ISO code standard";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
