package com.checkout.payment.rest.v1.assembly;

import com.checkout.payment.gateway.command.CardPaymentProcessCommand;
import com.checkout.payment.gateway.command.PaymentProcessCommand;
import com.checkout.payment.gateway.command.exception.ExpiredCardDateException;
import com.checkout.payment.gateway.model.CashAmount;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.card.CardPaymentMethodDetails;
import com.checkout.payment.rest.v1.request.CardPaymentRequest;
import com.checkout.payment.rest.v1.request.PaymentRequest;
import com.checkout.payment.rest.v1.response.CardPaymentResponse;
import com.checkout.payment.rest.v1.response.PaymentResponse;
import java.util.Currency;
import java.util.UUID;

public class PaymentAssembly {

  public static PaymentProcessCommand assemblePaymentRequestToPaymentProcessCommand(PaymentRequest paymentRequest)
      throws ExpiredCardDateException {
    return switch (paymentRequest.toPaymentMethodType()) {
      case CARD -> createCardPaymentProcessCommand(paymentRequest);
    };
  }

  public static PaymentResponse assemblePaymentToPaymentResponse(Payment payment){
    return switch (payment.getPaymentMethodType()) {
      case CARD -> createCardPaymentResponse(payment);
    };
  }

  private static PaymentResponse createCardPaymentResponse(Payment payment) {
    CardPaymentMethodDetails cardPaymentMethodDetails = (CardPaymentMethodDetails) payment.getPaymentMethodDetails();
    String currency = payment.getCashAmount().getCurrencyIso();
    int minorCashAmount = payment.getCashAmount().getMinorAmount();
    return new CardPaymentResponse(payment.getTransactionId(),
        payment.getIdempotencyKey(),payment.getStatus(), currency, minorCashAmount, payment.getPaymentMethodType(),
        cardPaymentMethodDetails.getLastFourCardDigits(), cardPaymentMethodDetails.getExpiryMonth(),
        cardPaymentMethodDetails.getExpiryYear());
  }

  private static PaymentProcessCommand createCardPaymentProcessCommand(
      PaymentRequest paymentRequest) throws ExpiredCardDateException {
    CardPaymentRequest cardPaymentRequest = (CardPaymentRequest) paymentRequest;
    UUID idempotencyKey = UUID.fromString(cardPaymentRequest.getIdempotencyKey());
    int minorCashAmount = Integer.parseInt(cardPaymentRequest.getAmount());
    long cardNumber = Long.parseLong(cardPaymentRequest.getCardNumber());
    int cvv = Integer.parseInt(cardPaymentRequest.getCvv());

    CashAmount cashAmount = new CashAmount(Currency.getInstance(paymentRequest.getCurrency()), minorCashAmount);
    return new CardPaymentProcessCommand(idempotencyKey,
        cashAmount,paymentRequest.toPaymentMethodType(), cardNumber,
        cardPaymentRequest.getExpiryMonth(), cardPaymentRequest.getExpiryYear(), cvv);
  }

}
