package com.checkout.payment.application;

import com.checkout.payment.configuration.ApplicationConfiguration;
import com.checkout.payment.configuration.CommonExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@Configuration
@EnableAutoConfiguration
@Import({
    ApplicationConfiguration.class,
    CommonExceptionHandler.class
})
public class PaymentGatewayApplication {

  public PaymentGatewayApplication() {
    log.info("PaymentGatewayApplication APP INITIALISED version={}",
        getClass().getPackage().getImplementationVersion());
  }

  public static void main(String[] args) {
    SpringApplication.run(PaymentGatewayApplication.class, args);
  }

}
