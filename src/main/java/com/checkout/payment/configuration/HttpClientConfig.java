package com.checkout.payment.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HttpClientConfig {
    private String host;
    private int port = -1;
}
