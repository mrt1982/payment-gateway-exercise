package com.checkout.payment.configuration;

import com.checkout.payment.gateway.command.PaymentCommand;
import com.checkout.payment.gateway.factory.payment.PaymentFactory;
import com.checkout.payment.gateway.factory.paymentmethod.PaymentMethodDetailsGeneratorFactory;
import com.checkout.payment.gateway.factory.paymentprocessor.PaymentProcessorFactory;
import com.checkout.payment.gateway.model.PaymentMethodType;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.checkout.payment.gateway.service.BankService;
import com.checkout.payment.gateway.service.CardPaymentProcessor;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import com.checkout.payment.gateway.service.PaymentGatewayServiceImpl;
import com.checkout.payment.gateway.service.PaymentProcessor;
import com.checkout.payment.infrastructure.persistance.SimpleInMemoryPaymentsRepository;
import com.checkout.payment.infrastructure.rest.BankServiceImpl;
import com.checkout.payment.rest.v1.PaymentGatewayController;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import java.util.HashMap;
import java.util.Map;

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
      PaymentProcessorFactory paymentProcessorFactory) {
    return new PaymentGatewayServiceImpl(paymentsRepository, paymentProcessorFactory);
  }

  @Bean
  public PaymentsRepository paymentsRepository() {
    return new SimpleInMemoryPaymentsRepository();
  }

  @Bean
  public BankService bankService(RestClient restClient) {
    return new BankServiceImpl(restClient);
  }

  @Bean
  RestClient restClient(HttpClientConfig httpClientConfig) {
    return RestClient.create(httpClientConfig.getHost() + ":" + httpClientConfig.getPort());
  }

  @Bean
  PaymentProcessorFactory paymentProcessorFactory(
      Map<PaymentMethodType, PaymentProcessor<? extends PaymentCommand>> paymentProcessors){
    return new PaymentProcessorFactory(paymentProcessors);
  }

  @Bean
  Map<PaymentMethodType, PaymentProcessor<? extends PaymentCommand>> paymentProcessors(BankService bankService){
    Map<PaymentMethodType, PaymentProcessor<? extends PaymentCommand>> paymentProcessors = new HashMap<>();
    paymentProcessors.put(PaymentMethodType.CARD, new CardPaymentProcessor(paymentsRepository(), processPaymentFactory(), bankService));
    return paymentProcessors;
  }

  @Bean
  PaymentFactory processPaymentFactory(){
    return new PaymentFactory(paymentMethodDetailsGeneratorFactory());
  }

  @Bean
  PaymentMethodDetailsGeneratorFactory paymentMethodDetailsGeneratorFactory(){
    return new PaymentMethodDetailsGeneratorFactory();
  }


}
