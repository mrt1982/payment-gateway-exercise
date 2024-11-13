package com.checkout.payment.infrastructure.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CardPaymentRequest {
  @JsonProperty("card_number")
  private final long cardNumber;
  @JsonProperty("expiry_date")
  private final String expiryDate;
  private final String currency;
  private final int amount;
  private final int cvv;

}
