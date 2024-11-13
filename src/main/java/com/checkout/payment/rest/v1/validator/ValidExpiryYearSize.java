package com.checkout.payment.rest.v1.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExpiryYearSizeValidator.class)
public @interface ValidExpiryYearSize {
  String message() default "Expiry Year must be 4 digits";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};

}
