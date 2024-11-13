package com.checkout.payment.infrastructure.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.checkout.payment.gateway.model.CashAmount;
import com.checkout.payment.gateway.model.PaymentStatus;
import com.checkout.payment.gateway.service.BankService;
import com.checkout.payment.gateway.service.exception.BankServiceException;
import com.checkout.payment.gateway.service.exception.InvalidBankPaymentDetailsException;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class BankServiceImplMediumTest {

  private WireMockServer wireMockServer;

  private BankService testObj;

  @BeforeEach
  void setUp() {
    this.wireMockServer = new WireMockServer(options()
        .dynamicPort()
    );
    this.wireMockServer.start();
    testObj = new BankServiceImpl(client());
  }

  @Test
  void authorisePayment_validCardDetails_authorised() {
    //Given
    wireMockServer.stubFor(post(urlPathEqualTo("/payments"))
        .willReturn(aResponse()
            .withHeader("Content-type", "application/json")
            .withStatus(200)
            .withBodyFile("bank-api-card-payment-authorised-response.json")));
    //When
    PaymentStatus cardPaymentStatus = testObj.authorisePayment(12345L, 04, 2025,
        new CashAmount(Currency.getInstance("GBP"), 150), 500);
    //Then
    assertThat(cardPaymentStatus, is(equalTo(PaymentStatus.AUTHORIZED)));
  }

  @Test
  void authorisePayment_validCardDetailsButNotAuthorised_declined() {
    //Given
    wireMockServer.stubFor(post(urlPathEqualTo("/payments"))
        .willReturn(aResponse()
            .withHeader("Content-type", "application/json")
            .withStatus(200)
            .withBodyFile("bank-api-card-payment-not-authorised-response.json")));
    //When
    PaymentStatus cardPaymentStatus = testObj.authorisePayment(12345L, 04, 2025,
        new CashAmount(Currency.getInstance("GBP"), 150), 500);
    //Then
    assertThat(cardPaymentStatus, is(equalTo(PaymentStatus.DECLINED)));
  }

  @Test
  void authorisePayment_badGateway_throwError() {
    //Given
    wireMockServer.stubFor(post(urlPathEqualTo("/payments"))
        .willReturn(aResponse()
            .withHeader("Content-type", "application/json")
            .withStatus(503)));
    //When
    assertThrows(BankServiceException.class, () -> testObj.authorisePayment(12345L, 04, 2025,
        new CashAmount(Currency.getInstance("GBP"), 150), 500));
  }

  @Test
  void authorisePayment_invalidCardPaymentDetails_throwBankServiceException() {
    //Given
    wireMockServer.stubFor(post(urlPathEqualTo("/payments"))
        .willReturn(aResponse()
            .withHeader("Content-type", "application/json")
            .withStatus(400)));
    //When
    assertThrows(
        InvalidBankPaymentDetailsException.class, () -> testObj.authorisePayment(12345L, 04, 2025,
            new CashAmount(Currency.getInstance("GBP"), 150), 500));
  }

  private RestClient client() {
    return RestClient.create("http://localhost:" + wireMockServer.port());
  }
}