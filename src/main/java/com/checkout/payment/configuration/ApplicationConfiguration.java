package com.checkout.payment.configuration;

import com.checkout.payment.gateway.factory.PaymentFactory;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.checkout.payment.gateway.service.BankService;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import com.checkout.payment.gateway.service.PaymentGatewayServiceImpl;
import com.checkout.payment.infrastructure.persistance.SimpleInMemoryPaymentsRepository;
import com.checkout.payment.infrastructure.rest.BankServiceImpl;
import com.checkout.payment.rest.v1.PaymentGatewayController;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ApplicationConfiguration {

  @Bean
  @ConfigurationProperties("http-clients.bank-api")
  public HttpClientConfig httpClientConfig() {
    return new HttpClientConfig();
  }

  @Bean
  Validator validator() {
    return Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Bean
  public PaymentGatewayController paymentGatewayController(
      PaymentGatewayService paymentGatewayService, Validator validator) {
    return new PaymentGatewayController(paymentGatewayService, validator);
  }

  @Bean
  public PaymentGatewayService paymentGatewayService(
      PaymentsRepository paymentsRepository,
      PaymentFactory paymentFactory,
      BankService bankService) {
    return new PaymentGatewayServiceImpl(paymentsRepository, paymentFactory, bankService);
  }

  @Bean
  public PaymentsRepository paymentsRepository() {
    return new SimpleInMemoryPaymentsRepository();
  }

  @Bean
  public PaymentFactory paymentFactory() {
    return new PaymentFactory();
  }

  @Bean
  public BankService bankService(RestClient restClient) {
    return new BankServiceImpl(restClient);
  }

  @Bean
  RestClient restClient(HttpClientConfig httpClientConfig) {
    return RestClient.create(httpClientConfig.getHost() + ":" + httpClientConfig.getPort());
  }
}
