package com.dudev.orderservice.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@RequiredArgsConstructor
@ConfigurationProperties("token")
public class TokenProperties {

    private Jwt jwt = new Jwt();
    private Refresh refresh = new Refresh();

    @Data
    public static class Jwt {
        private Integer expiration;
        private String salt;
    }

    @Data
    public static class Refresh {
        private Integer expiration;
    }
}
