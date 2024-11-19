package com.checkout.payment.rest.v1.request;

import com.checkout.payment.rest.v1.validator.ValidExpiryMonthSize;
import com.checkout.payment.rest.v1.validator.ValidExpiryYearSize;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class CardPaymentRequest extends PaymentRequest{
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

  @NotNull(message = "CVV is required")
  @Pattern(regexp = "\\d+", message = "CVV must only contain numeric characters")
  @Size(min = 3, max = 4, message = "CVV must be between 3 and 4 characters")
  private String cvv;

  @Override
  public String toString() {
    return "CardPaymentRequest{" +
        "idempotencyKey=" + idempotencyKey +
        "cardNumber=" + cardNumber.substring(cardNumber.length()- 4) +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        '}';
  }

}
