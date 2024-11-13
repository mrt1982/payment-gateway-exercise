package com.checkout.payment.infrastructure.rest;

import com.checkout.payment.gateway.model.CashAmount;
import com.checkout.payment.gateway.model.PaymentStatus;
import com.checkout.payment.gateway.service.BankService;
import com.checkout.payment.gateway.service.exception.BankServiceException;
import com.checkout.payment.gateway.service.exception.InvalidBankPaymentDetailsException;
import com.checkout.payment.infrastructure.rest.request.CardPaymentRequest;
import com.checkout.payment.infrastructure.rest.response.CardPaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {
  private final RestClient restClient;

  @Override
  public PaymentStatus authorisePayment(long cardNumber, int expiryMonth, int expiryYear,
      CashAmount cashAmount, int cvv) {
    String expiryDate = convertToExpiryDate(expiryMonth, expiryYear);
    CardPaymentRequest cardPaymentRequest = new CardPaymentRequest(cardNumber,expiryDate, cashAmount.getCurrencyIso(), cashAmount.getMinorAmount(), cvv);
    CardPaymentResponse cardPaymentResponse = restClient.post().uri("/payments")
        .contentType(MediaType.APPLICATION_JSON)
        .body(cardPaymentRequest)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
          log.error("Invalid/Bad Request in calling the acquirer bank");
          throw new InvalidBankPaymentDetailsException();
        })
        .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
          log.error("Communication problem with calling the acquirer bank");
          throw new BankServiceException();
        })
        .body(CardPaymentResponse.class);
    return convertCardPaymentResponse(cardPaymentResponse);
  }

  private PaymentStatus convertCardPaymentResponse(CardPaymentResponse cardPaymentResponse) {
    return cardPaymentResponse.isAuthorized() ? PaymentStatus.AUTHORIZED : PaymentStatus.DECLINED;
  }

  private String convertToExpiryDate(int expiryMonth, int expiryYear) {
    return String.format("%02d/%d", expiryMonth, expiryYear);
  }
}
