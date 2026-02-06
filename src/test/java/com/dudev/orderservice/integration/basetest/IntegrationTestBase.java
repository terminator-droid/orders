package com.dudev.orderservice.integration.basetest;

import com.dudev.orderservice.integration.annotation.IT;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;


@IT
public abstract class IntegrationTestBase {

    private static final PostgreSQLContainer postgres = new PostgreSQLContainer(
            DockerImageName.parse("postgres:16-alpine"));

    @BeforeAll
    static void initContainer() {
        postgres.start();
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgres::getJdbcUrl);
    }

}
