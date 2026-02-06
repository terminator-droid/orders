package com.dudev.orderservice.integration.annotation;

import com.dudev.orderservice.OrderServiceApplication;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = {OrderServiceApplication.class, JacksonAutoConfiguration.class})
@ActiveProfiles(profiles = "test")
@Transactional
public @interface IT {
}
