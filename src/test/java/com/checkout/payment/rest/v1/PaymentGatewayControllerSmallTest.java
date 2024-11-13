package com.checkout.payment.rest.v1;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.checkout.payment.gateway.command.ProcessPaymentCommand;
import com.checkout.payment.gateway.exception.ExpiredCardDateException;
import com.checkout.payment.gateway.model.CashAmount;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PaymentMethodDetails;
import com.checkout.payment.gateway.model.PaymentStatus;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import com.checkout.payment.gateway.service.exception.PaymentAlreadyProcessedException;
import com.checkout.payment.gateway.service.exception.PaymentIncongruentServiceException;
import com.checkout.payment.rest.v1.request.PaymentRequest;
import com.checkout.payment.rest.v1.response.PaymentResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.Year;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PaymentGatewayControllerSmallTest {
  @Mock
  private PaymentGatewayService paymentGatewayServiceMock;
  private PaymentGatewayController testObj;
  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
    testObj = new PaymentGatewayController(paymentGatewayServiceMock, validator);
  }

  @Test
  void getPaymentById_paymentExist_returnOk200() {
    //Given
    UUID paymentTransactionId = UUID.randomUUID();
    UUID idempotencyKey = UUID.randomUUID();
    int expiryMonth = 10;
    int expiryYear = 2024;
    CashAmount cashAmount = new CashAmount(Currency.getInstance("GBP"), 150);

    Payment payment = createValidPayment(idempotencyKey, 1234, expiryMonth, expiryYear, PaymentStatus.AUTHORIZED, cashAmount);
    when(paymentGatewayServiceMock.findPaymentsByTransactionId(paymentTransactionId)).thenReturn(
        Optional.of(payment));
    //When
    ResponseEntity<PaymentResponse> actualPaymentResponse = testObj.getPaymentById(paymentTransactionId);
    //Then
    assertThat(actualPaymentResponse.getStatusCode().value(), is(equalTo(200)));
  }

  @Test
  void getPaymentById_paymentDoesNotExist_returnNotFound404() {
    //Given
    UUID paymentTransactionId = UUID.randomUUID();
    when(paymentGatewayServiceMock.findPaymentsByTransactionId(paymentTransactionId)).thenReturn(
        Optional.empty());
    //When
    ResponseEntity<PaymentResponse> actualPaymentResponse = testObj.getPaymentById(paymentTransactionId);
    //Then
    assertThat(actualPaymentResponse.getStatusCode().value(), is(equalTo(404)));
  }

  @Test
  void createPayment_validPaymentRequest_returnOk201()
      throws ExpiredCardDateException, PaymentAlreadyProcessedException {
    //Given
    UUID idempotencyKey = UUID.randomUUID();
    int expiryMonth = 10;
    int expiryYear = Year.now().plusYears(1).getValue();
    CashAmount cashAmount = new CashAmount(Currency.getInstance("GBP"), 150);

    Payment expectedPayment = createValidPayment(idempotencyKey, 1023, expiryMonth, expiryYear, PaymentStatus.AUTHORIZED, cashAmount);
    when(paymentGatewayServiceMock.processPayment(any(ProcessPaymentCommand.class))).thenReturn(expectedPayment);
    PaymentRequest paymentRequest = PaymentRequest.builder()
        .idempotencyKey(idempotencyKey.toString())
        .cardNumber("12345678911023")
        .expiryMonth(expiryMonth)
        .expiryYear(expiryYear)
        .currency(cashAmount.getCurrencyIso())
        .amount(String.valueOf(cashAmount.getMinorAmount()))
        .cvv("500").build();
    //When
    ResponseEntity<PaymentResponse> actualPaymentResponse = testObj.createPayment(paymentRequest);
    //Then
    assertThat(actualPaymentResponse.getStatusCode().value(), is(equalTo(201)));
  }

  @ParameterizedTest
  @MethodSource("missingRequiredFieldsForPaymentRequests")
  void createPayment_missingRequiredFieldsForAPaymentRequest_ThrowConstraintViolationException(
      PaymentRequest paymentRequest) {
    //Given & When & Then
    assertThrows(ConstraintViolationException.class, () -> testObj.createPayment(paymentRequest));
  }

  @ParameterizedTest
  @MethodSource("invalidSizeFieldsForAPaymentRequest")
  void createPayment_invalidSizeFieldsForAPaymentRequest_ThrowConstraintViolationException(
      PaymentRequest paymentRequest) {
    //Given & When & Then
    assertThrows(ConstraintViolationException.class, () -> testObj.createPayment(paymentRequest));
  }

  @ParameterizedTest
  @MethodSource("invalidTypeFieldsForAPaymentRequest")
  void createPayment_invalidTypeFieldsForAPaymentRequest_ThrowConstraintViolationException(
      PaymentRequest paymentRequest) {
    //Given & When & Then
    assertThrows(ConstraintViolationException.class, () -> testObj.createPayment(paymentRequest));
  }

  @Test
  void createPayment_expiredDate_throwExpiredCardDateException() {
    //Given
    UUID idempotencyKey = UUID.randomUUID();
    int expiryMonth = 10;
    int expiryYear = Year.now().minusYears(1).getValue();
    CashAmount cashAmount = new CashAmount(Currency.getInstance("GBP"), 150);
    PaymentRequest paymentRequest = PaymentRequest.builder()
        .idempotencyKey(idempotencyKey.toString())
        .cardNumber("12345678911023")
        .expiryMonth(expiryMonth)
        .expiryYear(expiryYear)
        .currency(cashAmount.getCurrencyIso())
        .amount(String.valueOf(cashAmount.getMinorAmount()))
        .cvv("500").build();
    //When & Then
    assertThrows(ExpiredCardDateException.class, () -> testObj.createPayment(paymentRequest));
  }

  @Test
  void createPayment_paymentAlreadyProcessed_returnOk200()
      throws ExpiredCardDateException, PaymentAlreadyProcessedException {
    //Given
    UUID idempotencyKey = UUID.randomUUID();
    int expiryMonth = 10;
    int expiryYear = Year.now().plusYears(1).getValue();
    CashAmount cashAmount = new CashAmount(Currency.getInstance("GBP"), 150);

    Payment expectedPayment = createValidPayment(idempotencyKey, 1023, expiryMonth, expiryYear, PaymentStatus.AUTHORIZED, cashAmount);
    when(paymentGatewayServiceMock.processPayment(any(ProcessPaymentCommand.class))).thenThrow(PaymentAlreadyProcessedException.class);
    when(paymentGatewayServiceMock.findPaymentByIdempotencyId(idempotencyKey)).thenReturn(Optional.of(expectedPayment));
    PaymentRequest paymentRequest = PaymentRequest.builder()
        .idempotencyKey(idempotencyKey.toString())
        .cardNumber("12345678911023")
        .expiryMonth(expiryMonth)
        .expiryYear(expiryYear)
        .currency(cashAmount.getCurrencyIso())
        .amount(String.valueOf(cashAmount.getMinorAmount()))
        .cvv("500").build();
    //When
    ResponseEntity<PaymentResponse> actualPaymentResponse = testObj.createPayment(paymentRequest);
    //Then
    assertThat(actualPaymentResponse.getStatusCode().value(), is(equalTo(200)));
  }

  @Test
  void createPayment_incongruentPaymentAlreadyProcessed_throwPaymentIncongruentServiceException()
      throws PaymentAlreadyProcessedException {
    //Given
    UUID idempotencyKey = UUID.randomUUID();
    int expiryMonth = 10;
    int expiryYear = Year.now().plusYears(1).getValue();
    CashAmount cashAmount = new CashAmount(Currency.getInstance("GBP"), 150);

    when(paymentGatewayServiceMock.processPayment(any(ProcessPaymentCommand.class))).thenThrow(PaymentAlreadyProcessedException.class);
    PaymentRequest paymentRequest = PaymentRequest.builder()
        .idempotencyKey(idempotencyKey.toString())
        .cardNumber("12345678911023")
        .expiryMonth(expiryMonth)
        .expiryYear(expiryYear)
        .currency(cashAmount.getCurrencyIso())
        .amount(String.valueOf(cashAmount.getMinorAmount()))
        .cvv("500").build();
    //When & Then
    assertThrows(PaymentIncongruentServiceException.class, () -> testObj.createPayment(paymentRequest));
  }

  private Payment createValidPayment(UUID idempotencyKey, int lastFourCardDigits, int expiryMonth, int expiryYear, PaymentStatus paymentStatus, CashAmount cashAmount) {
    PaymentMethodDetails paymentMethodDetails = new PaymentMethodDetails(lastFourCardDigits, expiryMonth, expiryYear);
    return new Payment(idempotencyKey, paymentStatus, cashAmount, paymentMethodDetails);
  }

  private static Stream<Arguments> invalidTypeFieldsForAPaymentRequest() {
    return Stream.of(
        Arguments.of(PaymentRequest.builder()
            .idempotencyKey("1234")
            .cardNumber("12345678912345")
            .expiryMonth(10)
            .expiryYear(2024)
            .currency("GBP")
            .amount("150")
            .cvv("500")
            .build()),
        Arguments.of(PaymentRequest.builder()
            .idempotencyKey(UUID.randomUUID().toString())
            .cardNumber("12345678910234x")
            .expiryMonth(10)
            .expiryYear(2024)
            .currency("GBP")
            .amount("150")
            .cvv("500")
            .build()),
        Arguments.of(PaymentRequest.builder()
            .idempotencyKey(UUID.randomUUID().toString())
            .cardNumber("12345678910234")
            .expiryMonth(10)
            .expiryYear(2024)
            .currency("ZZZ")
            .amount("1500")
            .cvv("500")
            .build()),
        Arguments.of(PaymentRequest.builder()
            .idempotencyKey(UUID.randomUUID().toString())
            .cardNumber("12345678910234")
            .expiryMonth(10)
            .expiryYear(2024)
            .currency("GBP")
            .amount("150.0")
            .cvv("500")
            .build()),
        Arguments.of(PaymentRequest.builder()
            .idempotencyKey(UUID.randomUUID().toString())
            .cardNumber("123456789012345")
            .expiryMonth(10)
            .expiryYear(2024)
            .currency("GBP")
            .amount("150")
            .cvv("500x")
            .build()));
  }
  private static Stream<Arguments> invalidSizeFieldsForAPaymentRequest() {
    return Stream.of(
        Arguments.of(PaymentRequest.builder()
            .idempotencyKey(UUID.randomUUID().toString())
            .cardNumber("123456789012")
            .expiryMonth(10)
            .expiryYear(2024)
            .currency("GBP")
            .amount("150")
            .cvv("500")
            .build()),
        Arguments.of(PaymentRequest.builder()
            .idempotencyKey(UUID.randomUUID().toString())
            .cardNumber("12345678912345678901")
            .expiryMonth(10)
            .expiryYear(2024)
            .currency("GBP")
            .amount("150")
            .cvv("500")
            .build()),
        Arguments.of(PaymentRequest.builder()
            .idempotencyKey(UUID.randomUUID().toString())
            .cardNumber("123456789012345")
            .expiryMonth(13)
            .expiryYear(2024)
            .currency("GBP")
            .amount("150")
            .cvv("500")
            .build()),
        Arguments.of(PaymentRequest.builder()
            .idempotencyKey(UUID.randomUUID().toString())
            .cardNumber("123456789012345")
            .expiryMonth(10)
            .expiryYear(20245)
            .currency("GBP")
            .amount("150")
            .cvv("500")
            .build()),
        Arguments.of(PaymentRequest.builder()
            .idempotencyKey(UUID.randomUUID().toString())
            .cardNumber("123456789012345")
            .expiryMonth(10)
            .expiryYear(2024)
            .currency("GBPP")
            .amount("150")
            .cvv("500")
            .build()),
        Arguments.of(PaymentRequest.builder()
            .idempotencyKey(UUID.randomUUID().toString())
            .cardNumber("123456789012345")
            .expiryMonth(10)
            .expiryYear(2024)
            .currency("GBP")
            .amount("150")
            .cvv("50")
            .build()),
        Arguments.of(PaymentRequest.builder()
            .idempotencyKey(UUID.randomUUID().toString())
            .cardNumber("123456789012345")
            .expiryMonth(10)
            .expiryYear(2024)
            .currency("GBP")
            .amount("150")
            .cvv("50000")
            .build()));
  }

  private static Stream<Arguments> missingRequiredFieldsForPaymentRequests() {
    return Stream.of(
        Arguments.of(PaymentRequest.builder()
            .cardNumber("12345678912345")
            .expiryMonth(10)
            .expiryYear(2024)
            .currency("GBP")
            .amount("150")
            .cvv("500")
            .build()),
        Arguments.of(PaymentRequest.builder()
            .idempotencyKey(UUID.randomUUID().toString())
            .expiryMonth(10)
            .expiryYear(2024)
            .currency("GBP")
            .amount("150")
            .cvv("500")
            .build()),
        Arguments.of(PaymentRequest.builder()
            .idempotencyKey(UUID.randomUUID().toString())
            .cardNumber("12345678912345")
            .expiryYear(2024)
            .currency("GBP")
            .amount("150")
            .cvv("500")
            .build()),
        Arguments.of(PaymentRequest.builder()
            .idempotencyKey(UUID.randomUUID().toString())
            .cardNumber("12345678912345")
            .expiryMonth(10)
            .currency("GBP")
            .amount("150")
            .cvv("500")
            .build()),
        Arguments.of(PaymentRequest.builder()
            .idempotencyKey(UUID.randomUUID().toString())
            .cardNumber("12345678912345")
            .expiryMonth(10)
            .expiryYear(2024)
            .amount("150")
            .cvv("500")
            .build()),
        Arguments.of(PaymentRequest.builder()
            .idempotencyKey(UUID.randomUUID().toString())
            .cardNumber("12345678912345")
            .expiryMonth(10)
            .expiryYear(2024)
            .currency("")
            .amount("150")
            .cvv("500")
            .build()),
        Arguments.of(PaymentRequest.builder()
            .idempotencyKey(UUID.randomUUID().toString())
            .cardNumber("12345678912345")
            .expiryMonth(10)
            .expiryYear(2024)
            .currency("GBP")
            .cvv("500")
            .build()),
        Arguments.of(PaymentRequest.builder()
            .idempotencyKey(UUID.randomUUID().toString())
            .cardNumber("12345678912345")
            .expiryMonth(10)
            .expiryYear(2024)
            .currency("GBP")
            .amount("150")
            .build()));
  }
}