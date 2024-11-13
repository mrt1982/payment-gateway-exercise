package com.checkout.payment.rest.v1.validator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExpiryMonthSizeValidator.class)
public @interface ValidExpiryMonthSize {
  String message() default "Expiry Month must be between 1 and 12 digits";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}