package com.checkout.payment.rest.v1.request;

import com.checkout.payment.gateway.model.PaymentMethodType;
import com.checkout.payment.rest.v1.validator.ValidCurrencyIso;
import com.checkout.payment.rest.v1.validator.ValueOfEnum;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "paymentMethodType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CardPaymentRequest.class, name = "CARD")
})
public abstract class PaymentRequest implements Serializable {
  @NotNull(message = "idempotencyKey is required")
  @Pattern(
      regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
      message = "Invalid idempotencyKey UUID format"
  )
  protected String idempotencyKey;

  @NotNull(message = "Payment Method Type is required")
  @ValueOfEnum(enumClass = PaymentMethodType.class, message = "Invalid Payment Method type")
  protected String paymentMethodType;

  @NotNull(message = "Currency is required")
  @NotBlank(message = "Currency cannot be empty")
  @Size(min = 3, max = 3, message = "Currency number must be 3 in size")
  @ValidCurrencyIso
  protected String currency;

  @NotNull(message = "Amount is required")
  @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "Amount must be a numeric integer with no decimal places")
  protected String amount;

  public PaymentMethodType toPaymentMethodType() {
    return PaymentMethodType.valueOf(paymentMethodType);
  }
}
