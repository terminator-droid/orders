package com.dudev.orderservice.configuration;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((req, body, execution) -> {
            req.getHeaders().add("X-Trace-Id", MDC.get("traceId"));
            return execution.execute(req, body);
        });
        return restTemplate;
    }
}
