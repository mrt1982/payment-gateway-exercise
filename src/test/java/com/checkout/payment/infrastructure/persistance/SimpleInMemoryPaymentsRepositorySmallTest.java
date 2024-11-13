package com.checkout.payment.infrastructure.persistance;

import com.checkout.payment.gateway.model.CashAmount;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PaymentMethodDetails;
import com.checkout.payment.gateway.model.PaymentStatus;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class SimpleInMemoryPaymentsRepositorySmallTest {
  private PaymentsRepository testObj;

  @BeforeEach
  void setup() {
    testObj = new SimpleInMemoryPaymentsRepository();
  }

  @Test
  void createPayment_validPayment_success() {
    //Given
    UUID idempotencyKey = UUID.randomUUID();
    Payment payment = createValidPayment(idempotencyKey);

    //When
    Payment actualPayment = testObj.createPayment(payment);
    //Then
    assertThat(actualPayment, is(notNullValue()));
    assertThat(actualPayment.getTransactionId(), is(notNullValue()));
  }

  @Test
  void getByTransactionId_paymentExist_returnPayment() {
    //Given
    UUID idempotencyKey = UUID.randomUUID();
    Payment payment = createValidPayment(idempotencyKey);
    Payment persistedPayment = testObj.createPayment(payment);
    UUID transactionId = persistedPayment.getTransactionId();
    //When
    Optional<Payment> actualPaymentOpt = testObj.getByTransactionId(transactionId);
    //Then
    assertThat(actualPaymentOpt.isPresent(), is(equalTo(true)));
    assertThat(actualPaymentOpt.get().getTransactionId(), is(equalTo(transactionId)));
  }

  @Test
  void getByTransactionId_paymentDoesNotExist_returnEmptyPayment() {
    //Given & When
    Optional<Payment> actualPaymentOpt = testObj.getByTransactionId(UUID.randomUUID());
    //Then
    assertThat(actualPaymentOpt.isEmpty(), is(equalTo(true)));
  }

  @Test
  void getByIdempotencyKey_paymentExist_returnPayment() {
    //Given
    UUID idempotencyKey = UUID.randomUUID();
    Payment payment = createValidPayment(idempotencyKey);
    testObj.createPayment(payment);
    //When
    Optional<Payment> actualPaymentOpt = testObj.getByIdempotencyKey(idempotencyKey);
    //Then
    assertThat(actualPaymentOpt.isPresent(), is(equalTo(true)));
    assertThat(actualPaymentOpt.get().getIdempotencyKey(), is(equalTo(idempotencyKey)));
  }

  @Test
  void getByIdempotencyKey_paymentDoesNotExist_returnEmptyPayment() {
    //Given & When
    Optional<Payment> actualPaymentOpt = testObj.getByIdempotencyKey(UUID.randomUUID());
    //Then
    assertThat(actualPaymentOpt.isEmpty(), is(equalTo(true)));
  }

  private Payment createValidPayment(UUID idempotencyKey) {
    PaymentMethodDetails cardPaymentMethodDetails = new PaymentMethodDetails(1234, 10, 2025);
    return new Payment(idempotencyKey, PaymentStatus.AUTHORIZED, new CashAmount(Currency.getInstance("GBP"), 150), cardPaymentMethodDetails);
  }
}