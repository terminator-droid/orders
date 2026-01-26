package com.dudev.orderservice.service;

import org.springframework.boot.CommandLineRunner;

public interface DataInitializerService extends CommandLineRunner {
    void run(String... args) throws Exception;
}
