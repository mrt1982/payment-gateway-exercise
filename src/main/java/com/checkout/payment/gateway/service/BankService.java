package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.model.CashAmount;
import com.checkout.payment.gateway.model.PaymentStatus;

public interface BankService {
  PaymentStatus authorisePayment(long cardNumber, int expiryMonth, int expiryYear, CashAmount cashAmount, int cvv);

}
