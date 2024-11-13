package com.checkout.payment.rest.v1;

import com.checkout.payment.gateway.command.ProcessPaymentCommand;
import com.checkout.payment.gateway.exception.ExpiredCardDateException;
import com.checkout.payment.gateway.model.CashAmount;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import com.checkout.payment.gateway.service.exception.PaymentAlreadyProcessedException;
import com.checkout.payment.gateway.service.exception.PaymentIncongruentServiceException;
import com.checkout.payment.rest.v1.request.PaymentRequest;
import com.checkout.payment.rest.v1.response.PaymentResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Currency;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("api")
public class PaymentGatewayController {

  private final PaymentGatewayService paymentGatewayService;
  private final Validator validator;

  public PaymentGatewayController(PaymentGatewayService paymentGatewayService, Validator validator) {
    this.paymentGatewayService = paymentGatewayService;
    this.validator = validator;
  }

  @GetMapping("/payment/{id}")
  public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable UUID id) {
    Optional<Payment> payment = paymentGatewayService.findPaymentsByTransactionId(id);
    return payment.map(value -> new ResponseEntity<>(PaymentResponse.from(value), HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PostMapping("/payment")
  public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest paymentRequest)
      throws ExpiredCardDateException {
    validatePaymentRequest(paymentRequest);
    try {
      Payment payment = processPayment(paymentRequest);
      return ResponseEntity.status(HttpStatus.CREATED).body(PaymentResponse.from(payment));
    } catch (PaymentAlreadyProcessedException e) {
      return handleAlreadyProcessedPayment(paymentRequest);
    }
  }

  private ResponseEntity<PaymentResponse> handleAlreadyProcessedPayment(
      PaymentRequest paymentRequest) {
    UUID idempotencyKey = UUID.fromString(paymentRequest.getIdempotencyKey());
    Optional<Payment> processedPayment = paymentGatewayService.findPaymentByIdempotencyId(idempotencyKey);
    if(processedPayment.isPresent()){
      log.info("Payment has already been processed, id={} and idempotencyKey={}", processedPayment.get().getTransactionId(), idempotencyKey);
      return ResponseEntity.status(HttpStatus.OK)
          .body(PaymentResponse.from(processedPayment.get()));
    }
    log.error("Incongruent conflict. Payment already been processed but doesn't exist. idempotencyKey={}", idempotencyKey);
    throw new PaymentIncongruentServiceException();
  }

  private Payment processPayment(PaymentRequest paymentRequest)
      throws ExpiredCardDateException, PaymentAlreadyProcessedException {
    return paymentGatewayService.processPayment(buildProcessPaymentCommand(paymentRequest));
  }
  private ProcessPaymentCommand buildProcessPaymentCommand(PaymentRequest paymentRequest)
      throws ExpiredCardDateException {
    UUID idempotencyKey = UUID.fromString(paymentRequest.getIdempotencyKey());
    Currency currency = Currency.getInstance(paymentRequest.getCurrency());
    CashAmount cashAmount = new CashAmount(currency, Integer.parseInt(paymentRequest.getAmount()));
    long cardNumber = Long.parseLong(paymentRequest.getCardNumber());
    int cvv = Integer.parseInt(paymentRequest.getCvv());
    return new ProcessPaymentCommand(idempotencyKey,
        cashAmount,
        cardNumber,
        paymentRequest.getExpiryMonth(),
        paymentRequest.getExpiryYear(),
        cvv);
  }

  private void validatePaymentRequest(PaymentRequest paymentRequest) {
    Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(paymentRequest);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}
