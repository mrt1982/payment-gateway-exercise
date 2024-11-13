package com.checkout.payment.rest.v1.response;

import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PaymentMethodDetails;
import com.checkout.payment.gateway.model.PaymentStatus;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PaymentResponse {
  private UUID id;
  private UUID idempotencyKey;
  private PaymentStatus status;
  private int cardNumberLastFour;
  private int expiryMonth;
  private int expiryYear;
  private String currency;
  private int amount;

  public static PaymentResponse from(Payment payment) {
    PaymentMethodDetails paymentMethodDetails = payment.getPaymentMethodDetails();
    return new PaymentResponse(payment.getTransactionId(),
        payment.getIdempotencyKey(),
        payment.getStatus(),
        paymentMethodDetails.getLastFourCardDigits(),
        paymentMethodDetails.getExpiryMonth(),
        paymentMethodDetails.getExpiryYear(),
        payment.getCashAmount().getCurrencyIso(),
        payment.getCashAmount().getMinorAmount());
  }

  @Override
  public String toString() {
    return "GetPaymentResponse{" +
        "id=" + id +
        ", idempotencyKey=" + idempotencyKey +
        ", status=" + status +
        ", cardNumberLastFour=" + cardNumberLastFour +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        '}';
  }
}
