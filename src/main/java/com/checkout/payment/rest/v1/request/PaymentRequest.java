package com.checkout.payment.rest.v1.request;

import com.checkout.payment.rest.v1.validator.ValidCurrencyIso;
import com.checkout.payment.rest.v1.validator.ValidExpiryMonthSize;
import com.checkout.payment.rest.v1.validator.ValidExpiryYearSize;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NotNull(message = "PaymentRequest is required")
public class PaymentRequest implements Serializable {
  @NotNull(message = "idempotencyKey is required")
  @Pattern(
      regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
      message = "Invalid idempotencyKey UUID format"
  )
  private String idempotencyKey;

  @JsonProperty("card_number")
  @Size(min = 14, max = 19, message = "Card number must be between 14 and 19 characters")
  @Pattern(regexp = "\\d+", message = "Card number must only contain numeric characters")
  @NotNull(message = "Card Number is required")
  private String cardNumber;

  @JsonProperty("expiry_month")
  @NotNull(message = "Expiry Month is required")
  @ValidExpiryMonthSize
  private Integer expiryMonth;

  @JsonProperty("expiry_year")
  @NotNull(message = "Expiry Year is required")
  @ValidExpiryYearSize
  private Integer expiryYear;

  @NotNull(message = "Currency is required")
  @NotBlank(message = "Currency cannot be empty")
  @Size(min = 3, max = 3, message = "Currency number must be 3 in size")
  @ValidCurrencyIso
  private String currency;

  @NotNull(message = "Amount is required")
  @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "Amount must be a numeric integer with no decimal places")
  private String amount;

  @NotNull(message = "CVV is required")
  @Pattern(regexp = "\\d+", message = "CVV must only contain numeric characters")
  @Size(min = 3, max = 4, message = "CVV must be between 3 and 4 characters")
  private String cvv;

  @Override
  public String toString() {
    return "PaymentRequest{" +
        "idempotencyKey=" + idempotencyKey +
        "cardNumberLastFour=" + cardNumber +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        ", cvv=" + cvv +
        '}';
  }
}
