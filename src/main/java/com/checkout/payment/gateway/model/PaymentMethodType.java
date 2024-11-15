package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethodType {
  CARD("Card");

  private final String name;

  PaymentMethodType(String name) {
    this.name = name;
  }

  @JsonValue
  public String getName() {
    return this.name;
  }
}
