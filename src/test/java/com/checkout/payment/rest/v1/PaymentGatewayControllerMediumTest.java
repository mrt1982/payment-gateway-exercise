package com.checkout.payment.rest.v1;


import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.checkout.payment.AbstractMediumTest;
import com.checkout.payment.gateway.model.PaymentStatus;
import com.checkout.payment.rest.v1.request.PaymentRequest;
import com.checkout.payment.rest.v1.response.PaymentResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import java.time.Year;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

class PaymentGatewayControllerMediumTest extends AbstractMediumTest {

  @Test
  void createPayment_validPaymentRequest_returnPaymentResponseWith201()
      throws JsonProcessingException, JSONException {
    //Given
    setUpAuthorisedBankResponse(201, "bank-api-card-payment-authorised-response.json");
    PaymentRequest paymentRequest = PaymentRequest.builder()
        .idempotencyKey(UUID.randomUUID().toString())
        .cardNumber("12345678912345")
        .expiryMonth(10)
        .expiryYear(Year.now().plusYears(1).getValue())
        .currency("GBP")
        .amount("150")
        .cvv("500").build();
    //When
    Response response = given()
        .log().all()
        .body(om.writeValueAsString(paymentRequest))
        .contentType(JSON)
        .expect()
        .statusCode(201)
        .when()
        .post("/payment").andReturn();
    //Then
    JSONObject jsonObject = new JSONObject(response.getBody().asString());
    PaymentResponse actualPaymentResponse = om.readValue(jsonObject.toString(),
        PaymentResponse.class);
    assertThat(actualPaymentResponse, is(notNullValue()));
    assertThat(actualPaymentResponse.getStatus(), is(equalTo(PaymentStatus.AUTHORIZED)));
  }

  @Test
  void createPayment_paymentRequestWithUnauthorisedBankDetails_returnPaymentResponseWith201()
      throws JsonProcessingException, JSONException {
    //Given
    setUpAuthorisedBankResponse(201, "bank-api-card-payment-not-authorised-response.json");
    PaymentRequest paymentRequest = PaymentRequest.builder()
        .idempotencyKey(UUID.randomUUID().toString())
        .cardNumber("12345678912345")
        .expiryMonth(10)
        .expiryYear(Year.now().plusYears(1).getValue())
        .currency("GBP")
        .amount("150")
        .cvv("500").build();
    //When
    Response response = given()
        .log().all()
        .body(om.writeValueAsString(paymentRequest))
        .contentType(JSON)
        .expect()
        .statusCode(201)
        .when()
        .post("/payment").andReturn();
    //Then
    JSONObject jsonObject = new JSONObject(response.getBody().asString());
    PaymentResponse actualPaymentResponse = om.readValue(jsonObject.toString(),
        PaymentResponse.class);
    assertThat(actualPaymentResponse, is(notNullValue()));
    assertThat(actualPaymentResponse.getStatus(), is(equalTo(PaymentStatus.DECLINED)));
  }

  @Test
  void createPayment_missingCardNumber_return400AndRequiredCardNumberErrorCode()
      throws JsonProcessingException {
    //Given
    PaymentRequest paymentRequest = PaymentRequest.builder()
        .idempotencyKey(UUID.randomUUID().toString())
        .expiryMonth(10)
        .expiryYear(Year.now().plusYears(1).getValue())
        .currency("GBP")
        .amount("150")
        .cvv("500").build();
    //When & Then
    given()
        .log().all()
        .body(om.writeValueAsString(paymentRequest))
        .contentType(JSON)
        .expect()
        .statusCode(400)
        .when()
        .post("/payment").then().log().all()
        .assertThat().statusCode(400).contentType(JSON)
        .assertThat().body(notNullValue())
        .assertThat().body("errors[0].code", equalTo("cardNumber"))
        .assertThat().body("errors[0].message", equalTo("Card Number is required"));
  }

  @Test
  void createPayment_cardExpiryDateExpired_return400AndCardExpiredErrorCode()
      throws JsonProcessingException {
    //Given
    PaymentRequest paymentRequest = PaymentRequest.builder()
        .idempotencyKey(UUID.randomUUID().toString())
        .cardNumber("12345678912345")
        .expiryMonth(10)
        .expiryYear(Year.now().minusYears(1).getValue())
        .currency("GBP")
        .amount("150")
        .cvv("500").build();
    //When & Then
    given()
        .log().all()
        .body(om.writeValueAsString(paymentRequest))
        .contentType(JSON)
        .expect()
        .statusCode(400)
        .when()
        .post("/payment").then().log().all()
        .assertThat().statusCode(400).contentType(JSON)
        .assertThat().body(notNullValue())
        .assertThat().body("errors[0].code", equalTo("card.expiry.date.expired"));
  }

  @Test
  void createPayment_processSamePaymentMoreThanOnce_returnPaymentResponseWith200()
      throws JsonProcessingException, JSONException {
    //Given
    String idempotencyKey = UUID.randomUUID().toString();
    setUpAuthorisedBankResponse(201, "bank-api-card-payment-authorised-response.json");
    PaymentRequest paymentRequest = PaymentRequest.builder()
        .idempotencyKey(idempotencyKey)
        .cardNumber("12345678912345")
        .expiryMonth(10)
        .expiryYear(Year.now().plusYears(1).getValue())
        .currency("GBP")
        .amount("150")
        .cvv("500").build();
    given()
        .log().all()
        .body(om.writeValueAsString(paymentRequest))
        .contentType(JSON)
        .expect()
        .statusCode(201)
        .when()
        .post("/payment");
    //When
    Response response = given()
        .log().all()
        .body(om.writeValueAsString(paymentRequest))
        .contentType(JSON)
        .expect()
        .statusCode(200)
        .when()
        .post("/payment").andReturn();
    //Then
    JSONObject jsonObject = new JSONObject(response.getBody().asString());
    PaymentResponse actualPaymentResponse = om.readValue(jsonObject.toString(),
        PaymentResponse.class);
    assertThat(actualPaymentResponse, is(notNullValue()));
    assertThat(actualPaymentResponse.getStatus(), is(equalTo(PaymentStatus.AUTHORIZED)));
    assertThat(idempotencyKey, is(equalTo(actualPaymentResponse.getIdempotencyKey().toString())));
  }

  @Test
  void createPayment_badGatewayCallingBank_return502()
      throws JsonProcessingException {
    //Given
    String idempotencyKey = UUID.randomUUID().toString();
    setUpAuthorisedBankResponse(502, "bank-api-card-payment-authorised-response.json");
    //When & Then
    PaymentRequest paymentRequest = PaymentRequest.builder()
        .idempotencyKey(idempotencyKey)
        .cardNumber("12345678912345")
        .expiryMonth(10)
        .expiryYear(Year.now().plusYears(1).getValue())
        .currency("GBP")
        .amount("150")
        .cvv("500").build();
    //When & Then
    given()
        .log().all()
        .body(om.writeValueAsString(paymentRequest))
        .contentType(JSON)
        .expect()
        .statusCode(502)
        .when()
        .post("/payment").then().log().all()
        .assertThat().statusCode(502).contentType(JSON)
        .assertThat().body(notNullValue())
        .assertThat().body("errors[0].code", equalTo("bad.gateway.error"));
  }

  @Test
  void createPayment_badGatewayCallingBank_return503()
      throws JsonProcessingException {
    //Given
    String idempotencyKey = UUID.randomUUID().toString();
    setUpAuthorisedBankResponse(400, "bank-api-card-payment-authorised-response.json");
    //When & Then
    PaymentRequest paymentRequest = PaymentRequest.builder()
        .idempotencyKey(idempotencyKey)
        .cardNumber("12345678912345")
        .expiryMonth(10)
        .expiryYear(Year.now().plusYears(1).getValue())
        .currency("GBP")
        .amount("150")
        .cvv("500").build();
    //When & Then
    given()
        .log().all()
        .body(om.writeValueAsString(paymentRequest))
        .contentType(JSON)
        .expect()
        .statusCode(503)
        .when()
        .post("/payment").then().log().all()
        .assertThat().statusCode(503).contentType(JSON)
        .assertThat().body(notNullValue())
        .assertThat().body("errors[0].code", equalTo("internal.server.error"));
  }

  @Test
  void getPaymentById_paymentExists_returns200() throws JsonProcessingException, JSONException {
    // Given
    String idempotencyKey = UUID.randomUUID().toString();
    setUpAuthorisedBankResponse(201, "bank-api-card-payment-authorised-response.json");
    PaymentRequest paymentRequest = PaymentRequest.builder()
        .idempotencyKey(idempotencyKey)
        .cardNumber("12345678912345")
        .expiryMonth(10)
        .expiryYear(Year.now().plusYears(1).getValue())
        .currency("GBP")
        .amount("150")
        .cvv("500").build();
    PaymentResponse createdPaymentResponse = postPaymentRequest(paymentRequest);

    // When
    PaymentResponse fetchedPaymentResponse = fetchPaymentById(createdPaymentResponse.getId());

    // Then
    assertThat(fetchedPaymentResponse, is(notNullValue()));
    assertThat(fetchedPaymentResponse.getStatus(), is(equalTo(PaymentStatus.AUTHORIZED)));
    assertThat(fetchedPaymentResponse.getId(), is(equalTo(createdPaymentResponse.getId())));
  }

  @Test
  void getPaymentById_paymentDoesNotExists_returns404() {
    // Given & When & Then
    given()
        .log().all()
        .contentType(JSON)
        .pathParam("id", UUID.randomUUID().toString())
        .expect()
        .statusCode(404)
        .when()
        .get("/payment/{id}");
  }

  private PaymentResponse postPaymentRequest(PaymentRequest paymentRequest)
      throws JsonProcessingException, JSONException {
    Response response = given()
        .log().all()
        .body(om.writeValueAsString(paymentRequest))
        .contentType(JSON)
        .expect()
        .statusCode(201)
        .when()
        .post("/payment").andReturn();
    return parsePaymentResponse(response);
  }

  private PaymentResponse fetchPaymentById(UUID paymentId)
      throws JSONException, JsonProcessingException {
    Response response = given()
        .log().all()
        .contentType(JSON)
        .pathParam("id", paymentId.toString())
        .expect()
        .statusCode(200)
        .when()
        .get("/payment/{id}").andReturn();
    return parsePaymentResponse(response);
  }

  private PaymentResponse parsePaymentResponse(Response response)
      throws JsonProcessingException, JSONException {
    JSONObject jsonObject = new JSONObject(response.getBody().asString());
    return om.readValue(jsonObject.toString(), PaymentResponse.class);
  }

  private void setUpAuthorisedBankResponse(int statusCode, String accountResponseFileName) {
    WIRE_MOCK_SERVER.stubFor(post(urlPathEqualTo("/payments"))
        .willReturn(aResponse()
            .withHeader("Content-type", "application/json")
            .withStatus(statusCode)
            .withBodyFile(accountResponseFileName)));
  }
}