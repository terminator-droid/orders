package com.dudev.orderservice.service.impl;

import com.dudev.orderservice.model.User;
import com.dudev.orderservice.model.enums.Role;
import com.dudev.orderservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializerServiceImpl implements com.dudev.orderservice.service.DataInitializerService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${app.admin.password}")
    private String adminPassword;
    @Value("${app.admin.username}")
    private String adminUsername;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByUsername(adminUsername)) {
            User user = User.builder()
                    .role(Role.ADMIN)
                    .password(passwordEncoder.encode(adminPassword))
                    .username(adminUsername)
                    .build();
            userRepository.save(user);
        }
    }
}
