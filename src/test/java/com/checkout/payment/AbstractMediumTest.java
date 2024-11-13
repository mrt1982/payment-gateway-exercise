package com.checkout.payment;

import com.checkout.payment.application.PaymentGatewayApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = {PaymentGatewayApplication.class})
public abstract class AbstractMediumTest {
  protected ObjectMapper om = new ObjectMapper();
  protected static WireMockServer WIRE_MOCK_SERVER;
  @LocalServerPort
  private int serverPort;

  @BeforeAll
  static void intialiseApplication() {
    WIRE_MOCK_SERVER = new WireMockServer(options()
        .port(9000));
    WIRE_MOCK_SERVER.start();
  }

  @AfterAll
  static void tearDownApplication() {
    WIRE_MOCK_SERVER.stop();
  }

  @BeforeEach
  public void setUp() {
    WIRE_MOCK_SERVER.resetMappings();
    RestAssured.port = serverPort;
    RestAssured.basePath = "/payment-gateway-api";
  }
}
