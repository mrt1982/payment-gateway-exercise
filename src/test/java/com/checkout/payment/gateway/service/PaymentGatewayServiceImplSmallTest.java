package com.checkout.payment.gateway.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.checkout.payment.gateway.command.ProcessPaymentCommand;
import com.checkout.payment.gateway.exception.ExpiredCardDateException;
import com.checkout.payment.gateway.factory.PaymentFactory;
import com.checkout.payment.gateway.model.CashAmount;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.checkout.payment.gateway.service.exception.PaymentAlreadyProcessedException;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentGatewayServiceImplSmallTest {
  @Mock
  private PaymentsRepository paymentsRepositoryMock;

  @Mock
  private BankService bankServiceMock;
  private PaymentGatewayService testObj;

  @BeforeEach
  void setup() {
    testObj = new PaymentGatewayServiceImpl(paymentsRepositoryMock, new PaymentFactory(), bankServiceMock);
  }


  @Test
  void findPaymentsByTransactionId_paymentExistsForTransactionId_returnPayment() {
    //Given
    UUID transactionId = UUID.randomUUID();
    Payment payment = new Payment(UUID.randomUUID(),null,null,null);
    when(paymentsRepositoryMock.getByTransactionId(transactionId)).thenReturn(Optional.of(payment));
    //When
    Optional<Payment> actualPaymentOpt = testObj.findPaymentsByTransactionId(transactionId);
    //Then
    assertThat(actualPaymentOpt.isPresent(), is(equalTo(true)));
    verify(paymentsRepositoryMock, times(1)).getByTransactionId(transactionId);
  }

  @Test
  void findPaymentsByTransactionId_paymentDoesNotExistsForTransactionId_returnEmptyPayment() {
    //Given
    UUID transactionId = UUID.randomUUID();
    when(paymentsRepositoryMock.getByTransactionId(transactionId)).thenReturn(Optional.empty());
    //When
    Optional<Payment> actualPaymentOpt = testObj.findPaymentsByTransactionId(transactionId);
    //Then
    assertThat(actualPaymentOpt.isPresent(), is(equalTo(false)));
    verify(paymentsRepositoryMock, times(1)).getByTransactionId(transactionId);
  }

  @Test
  void findPaymentByIdempotencyId_paymentExistsForIdempotencyId_returnPayment() {
    //Given
    UUID idempotencyKey = UUID.randomUUID();
    Payment payment = new Payment(idempotencyKey,null,null,null);
    when(paymentsRepositoryMock.getByIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(payment));
    //When
    Optional<Payment> actualPaymentOpt = testObj.findPaymentByIdempotencyId(idempotencyKey);
    //Then
    assertThat(actualPaymentOpt.isPresent(), is(equalTo(true)));
    verify(paymentsRepositoryMock, times(1)).getByIdempotencyKey(idempotencyKey);
  }

  @Test
  void findPaymentByIdempotencyId_paymentDoesNotExistsForTransactionId_returnEmptyPayment() {
    //Given
    UUID idempotencyKey = UUID.randomUUID();
    when(paymentsRepositoryMock.getByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
    //When
    Optional<Payment> actualPaymentOpt = testObj.findPaymentByIdempotencyId(idempotencyKey);
    //Then
    assertThat(actualPaymentOpt.isPresent(), is(equalTo(false)));
    verify(paymentsRepositoryMock, times(1)).getByIdempotencyKey(idempotencyKey);
  }


  @Test
  void processPayment_paymentHasNotAlreadyBeenProcessed_returnPayment()
      throws ExpiredCardDateException, PaymentAlreadyProcessedException {
    //Given
    UUID idempotencyKey = UUID.randomUUID();
    when(paymentsRepositoryMock.getByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
    ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
    when(paymentsRepositoryMock.createPayment(paymentCaptor.capture()))
        .thenAnswer(invocation -> paymentCaptor.getValue());
    ProcessPaymentCommand processPaymentCommand = new ProcessPaymentCommand(idempotencyKey, new CashAmount(
        Currency.getInstance("GBP"), 2025), 123L, 10, 2025, 0);
    //When
    Payment actualPayment = testObj.processPayment(processPaymentCommand);
    //Then
    Payment capturedPayment = paymentCaptor.getValue();
    assertThat(actualPayment, is(notNullValue()));
    verify(paymentsRepositoryMock, times(1)).getByIdempotencyKey(idempotencyKey);
    verify(bankServiceMock, times(1)).authorisePayment(123L,10, 2025, new CashAmount(
        Currency.getInstance("GBP"), 2025), 0);
    verify(paymentsRepositoryMock, times(1)).createPayment(capturedPayment);
  }

  @Test
  void processPayment_paymentHasAlreadyBeenProcessed_throwPaymentAlreadyProcessedException()
      throws ExpiredCardDateException {
    //Given
    UUID idempotencyKey = UUID.randomUUID();
    Payment existingPayment = new Payment(idempotencyKey, null, null, null);
    when(paymentsRepositoryMock.getByIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(existingPayment));
    ProcessPaymentCommand processPaymentCommand = new ProcessPaymentCommand(idempotencyKey, null, 123L, 10, 2025, 0);
    //When & Then
    assertThrows(PaymentAlreadyProcessedException.class, () -> testObj.processPayment(processPaymentCommand));
  }
}