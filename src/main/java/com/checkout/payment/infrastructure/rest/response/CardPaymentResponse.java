package com.checkout.payment.infrastructure.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CardPaymentResponse {
  private final boolean authorized;
  @JsonProperty("authorization_code")
  private final String authorisedCode;

}
