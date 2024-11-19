package com.checkout.payment.rest.v1.response;

import com.checkout.payment.gateway.model.PaymentMethodType;
import com.checkout.payment.gateway.model.PaymentStatus;
import com.checkout.payment.rest.v1.request.CardPaymentRequest;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.util.UUID;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "paymentMethodType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CardPaymentResponse.class, name = "CARD")
})
public abstract class PaymentResponse implements Serializable {
  protected UUID id;
  protected UUID idempotencyKey;
  protected PaymentStatus status;
  protected String currency;
  protected int amount;


  protected PaymentMethodType paymentMethodType;

  public PaymentResponse(){
  }

  public PaymentResponse(UUID id, UUID idempotencyKey, PaymentStatus status, String currency,
      int amount, PaymentMethodType paymentMethodType) {
    this.id = id;
    this.idempotencyKey = idempotencyKey;
    this.status = status;
    this.currency = currency;
    this.amount = amount;
    this.paymentMethodType = paymentMethodType;
  }

  public UUID getId() {
    return id;
  }

  public UUID getIdempotencyKey() {
    return idempotencyKey;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public String getCurrency() {
    return currency;
  }

  public int getAmount() {
    return amount;
  }
  public PaymentMethodType getPaymentMethodType() {
    return paymentMethodType;
  }
}
