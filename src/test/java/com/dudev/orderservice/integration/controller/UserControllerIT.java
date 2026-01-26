package com.dudev.orderservice.integration.controller;

import com.dudev.orderservice.dto.UserDto;
import com.dudev.orderservice.integration.basetest.IntegrationTestBase;
import com.dudev.orderservice.model.User;
import com.dudev.orderservice.model.enums.Role;
import com.dudev.orderservice.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@AutoConfigureMockMvc
@WithMockUser(username = "testUsername", password = "testPassword", roles = {"ADMIN"})
public class UserControllerIT extends IntegrationTestBase {

    private static final String REST_URL = "/api/users";
    private static final String USERNAME = "testUsername";
    private static final String PASSWORD = "testPassword";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void findAllUsers_shouldReturnAllUsersWhenAdmin() throws Exception {
        List<User> testUsers = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> User.builder()
                        .username("username" + i)
                        .password("pass" + i)
                        .role(Role.USER)
                        .build())
                .peek(userRepository::saveAndFlush)  // сохраняем каждый
                .toList();

        String response = mockMvc.perform(get(REST_URL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDto> responseUsers = objectMapper.readValue(response, new TypeReference<ArrayList<UserDto>>() {
        });

        assertThat(responseUsers.size()).isEqualTo(testUsers.size() + 1);
        assertThat(testUsers.stream()
                .allMatch(it -> responseUsers.stream()
                        .map(UserDto::getUsername)
                        .toList()
                        .contains(it.getUsername()))).isTrue();
    }

    @Test
    void deleteUser_shouldDeleteUserWhenAdmin() throws Exception {
        User user = User.builder()
                .username("username")
                .password("pass")
                .role(Role.USER)
                .build();
        userRepository.saveAndFlush(user);

        mockMvc.perform(delete(REST_URL + "/{id}", user.getId()))
                .andExpect(status().isOk());

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }
}
