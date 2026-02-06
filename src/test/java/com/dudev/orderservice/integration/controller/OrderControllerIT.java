package com.dudev.orderservice.integration.controller;

import com.dudev.orderservice.dto.CreateOrderDto;
import com.dudev.orderservice.dto.OrderDto;
import com.dudev.orderservice.integration.basetest.IntegrationTestBase;
import com.dudev.orderservice.model.Order;
import com.dudev.orderservice.model.User;
import com.dudev.orderservice.model.enums.Role;
import com.dudev.orderservice.model.enums.Status;
import com.dudev.orderservice.repository.OrderRepository;
import com.dudev.orderservice.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@AutoConfigureMockMvc
@WithMockUser(username = "testUsername", password = "testPassword")
public class OrderControllerIT extends IntegrationTestBase {

    private static final String REST_URL = "/api/orders";

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createOrder_shouldSucceedForAuthenticatedUser() throws Exception {
        UUID userId = userRepository.save(User.builder()
                .username("testUsername")
                .password("testPassword")
                .role(Role.ADMIN)
                .build()).getId();
        CreateOrderDto order = CreateOrderDto.builder().description("test").build();

        mockMvc.perform(post(REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk());

        assertThat(orderRepository.findByUserId(userId)).hasSize(1);
    }

    @Test
    void findCurrentUserOrders_shouldReturnUserOrders() throws Exception {
        User user = userRepository.save(User.builder()
                .username("testUsername")
                .password("testPassword")
                .role(Role.ADMIN)
                .build());
        Order order = orderRepository.save(Order.builder()
                .status(Status.CREATED)
                .user(user)
                .description("test")
                .build());

        mockMvc.perform(get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(order.getId().toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAllOrders_shouldReturnAllForAdmin() throws Exception {
        User user = userRepository.save(User.builder()
                .username("testUsername")
                .password("testPassword")
                .role(Role.ADMIN)
                .build());
        List<Order> testOrders = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> Order.builder()
                        .status(Status.CREATED)
                        .user(user)
                        .description("test-" + i)
                        .build())
                .peek(orderRepository::saveAndFlush)  // сохраняем каждый
                .toList();

        String contentAsString = mockMvc.perform(get(REST_URL + "/all"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ArrayList<OrderDto> orderDtos = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });

        assertThat(orderDtos.size()).isEqualTo(testOrders.size());
        assertThat(testOrders.stream()
                .allMatch(it -> orderDtos.stream()
                        .map(OrderDto::getDescription)
                        .toList()
                        .contains(it.getDescription()))).isTrue();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateStatus_shouldUpdateForAdmin() throws Exception {
        User user = userRepository.save(User.builder()
                .username("testUsername")
                .password("testPassword")
                .role(Role.ADMIN)
                .build());
        Order order = orderRepository.save(Order.builder()
                .status(Status.CREATED)
                .user(user)
                .description("test")
                .build());

        mockMvc.perform(MockMvcRequestBuilders.patch(REST_URL + "/{id}/status", order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Status.IN_PROGRESS)))
                .andExpect(status().isOk());

        Status orderStatus = orderRepository.findByUserId(user.getId()).get(0).getStatus();

        assertThat(orderStatus).isEqualTo(Status.IN_PROGRESS);
    }

    @Test
    void deleteOrder_shouldSucceedForOwner() throws Exception {
        User user = userRepository.save(User.builder()
                .username("testUsername")
                .password("testPassword")
                .role(Role.ADMIN)
                .build());
        Order order = orderRepository.save(Order.builder()
                .user(user)
                .description("test")
                .build());

        mockMvc.perform(delete(REST_URL + "/{id}", order.getId()))
                .andExpect(status().isOk());

        assertThat(orderRepository.findById(order.getId())).isEmpty();
    }
}
