package com.checkout.payment.configuration;

import com.checkout.payment.gateway.exception.ExpiredCardDateException;
import com.checkout.payment.gateway.service.exception.BankServiceException;
import com.checkout.payment.gateway.service.exception.InvalidBankPaymentDetailsException;
import com.checkout.payment.gateway.service.exception.PaymentIncongruentServiceException;
import com.checkout.payment.rest.v1.response.ErrorListResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class CommonExceptionHandler {

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorListResponse> handleConstraintViolationException(ConstraintViolationException ex) {
    log.info("Bad Request Exception occurred", ex);
    return new ResponseEntity<>(generateErrorResponse(ex),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ExpiredCardDateException.class)
  public ResponseEntity<ErrorListResponse> handleExpiredCardDateException(ExpiredCardDateException ex) {
    log.info("Expired Card Date Bad Request", ex);
    return new ResponseEntity<>(new ErrorListResponse("card.expiry.date.expired", ex.getMessage()),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(PaymentIncongruentServiceException.class)
  public ResponseEntity<ErrorListResponse> handlePaymentIncongruentServiceException(PaymentIncongruentServiceException ex) {
    log.error("Payment cannot be processed due to incongruent error", ex);
    return new ResponseEntity<>(new ErrorListResponse("payment.incongruent.unprocessed", ex.getMessage()),
        HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(BankServiceException.class)
  public ResponseEntity<ErrorListResponse> handleBankServiceException(BankServiceException ex) {
    log.error("Communication error with calling Acquirer bank", ex);
    return new ResponseEntity<>(new ErrorListResponse("bad.gateway.error", ex.getMessage()),
        HttpStatus.BAD_GATEWAY);
  }

  @ExceptionHandler(InvalidBankPaymentDetailsException.class)
  public ResponseEntity<ErrorListResponse> handleInvalidBankPaymentDetailsException(InvalidBankPaymentDetailsException ex) {
    log.error("Invalid Unsupported Bad Request with calling Acquirer bank", ex);
    return new ResponseEntity<>(new ErrorListResponse("internal.server.error", "Error please try later"),
        HttpStatus.SERVICE_UNAVAILABLE);
  }

  private ErrorListResponse generateErrorResponse(final ConstraintViolationException exception) {
    return new ErrorListResponse(exception.getConstraintViolations().stream()
        .map(v -> new ErrorListResponse.ErrorResponse(getProperty(v), v.getMessage()))
        .collect(Collectors.toList()));
  }

  private String getProperty(ConstraintViolation v) {
    PathImpl path = (PathImpl) v.getPropertyPath();
    return path.getLeafNode().toString();
  }

}
