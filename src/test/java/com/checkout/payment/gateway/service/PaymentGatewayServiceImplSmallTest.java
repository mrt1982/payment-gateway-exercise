package com.checkout.payment.gateway.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.checkout.payment.gateway.command.CardPaymentProcessCommand;
import com.checkout.payment.gateway.command.exception.ExpiredCardDateException;
import com.checkout.payment.gateway.factory.paymentprocessor.PaymentProcessorFactory;
import com.checkout.payment.gateway.model.CashAmount;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.model.PaymentMethodType;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.checkout.payment.gateway.service.exception.PaymentAlreadyProcessedException;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentGatewayServiceImplSmallTest {
  @Mock
  private PaymentsRepository paymentsRepositoryMock;

  @Mock
  private PaymentProcessorFactory paymentProcessorFactoryMock;
  @Mock
  private PaymentProcessor<CardPaymentProcessCommand> cardPaymentProcessorPaymentProcessorMock;

  private PaymentGatewayService testObj;

  @BeforeEach
  void setup() {
    testObj = new PaymentGatewayServiceImpl(paymentsRepositoryMock, paymentProcessorFactoryMock);
  }


  @Test
  void findPaymentsByTransactionId_paymentExistsForTransactionId_returnPayment() {
    //Given
    UUID transactionId = UUID.randomUUID();
    Payment payment = new Payment(UUID.randomUUID(),null,null, null, null);
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
  void findPaymentByIdempotencyId_paymentExistsForIdempotencyKey_returnPayment() {
    //Given
    UUID idempotencyKey = UUID.randomUUID();
    Payment payment = new Payment(idempotencyKey,null,null,null,null);
    when(paymentsRepositoryMock.getByIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(payment));
    //When
    Optional<Payment> actualPaymentOpt = testObj.findPaymentByIdempotencyId(idempotencyKey);
    //Then
    assertThat(actualPaymentOpt.isPresent(), is(equalTo(true)));
    verify(paymentsRepositoryMock, times(1)).getByIdempotencyKey(idempotencyKey);
  }

  @Test
  void findPaymentByIdempotencyId_paymentDoesNotExistsForIdempotencyKey_returnEmptyPayment() {
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
  @SuppressWarnings("unchecked")
  void processCardPayment_paymentHasNotAlreadyBeenProcessed_returnPayment()
      throws PaymentAlreadyProcessedException, ExpiredCardDateException {
    //Given
    UUID idempotencyKey = UUID.randomUUID();
    when(paymentsRepositoryMock.getByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
    CardPaymentProcessCommand processPaymentCommand = new CardPaymentProcessCommand(idempotencyKey, new CashAmount(
        Currency.getInstance("GBP"), 2025), PaymentMethodType.CARD, 123L, 10, 2025, 0);
    Payment payment = new Payment(idempotencyKey,null,null,null,null);
    PaymentMethodType paymentMethodType = PaymentMethodType.CARD;
    when(paymentProcessorFactoryMock.getProcessor(paymentMethodType))
        .thenReturn((PaymentProcessor) cardPaymentProcessorPaymentProcessorMock);
    when(cardPaymentProcessorPaymentProcessorMock.processPayment(processPaymentCommand)).thenReturn(payment);
    //When
    Payment actualPayment = testObj.processPayment(processPaymentCommand);
    //Then
    assertThat(actualPayment, is(notNullValue()));
    verify(paymentsRepositoryMock, times(1)).getByIdempotencyKey(idempotencyKey);
    verify(paymentProcessorFactoryMock, times(1)).getProcessor(any());
    verify(paymentsRepositoryMock, times(1)).getByIdempotencyKey(idempotencyKey);
  }

  @Test
  void processCardPayment_paymentHasAlreadyBeenProcessed_throwPaymentAlreadyProcessedException()
      throws ExpiredCardDateException {
    //Given
    UUID idempotencyKey = UUID.randomUUID();
    Payment existingPayment = new Payment(idempotencyKey, null, null, PaymentMethodType.CARD,null);
    when(paymentsRepositoryMock.getByIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(existingPayment));
    CardPaymentProcessCommand processPaymentCommand = new CardPaymentProcessCommand(idempotencyKey, null, PaymentMethodType.CARD,123L, 10, 2025, 0);
    //When & Then
    assertThrows(PaymentAlreadyProcessedException.class, () -> testObj.processPayment(processPaymentCommand));
  }
}
