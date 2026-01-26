package com.dudev.orderservice.integration.controller;

import com.dudev.orderservice.dto.JwtAuthenticationResponse;
import com.dudev.orderservice.dto.RefreshAccessDto;
import com.dudev.orderservice.dto.SignInRequest;
import com.dudev.orderservice.dto.SignUpRequest;
import com.dudev.orderservice.dto.UserDtoFull;
import com.dudev.orderservice.integration.basetest.IntegrationTestBase;
import com.dudev.orderservice.model.RefreshToken;
import com.dudev.orderservice.model.User;
import com.dudev.orderservice.model.enums.Role;
import com.dudev.orderservice.repository.RefreshTokenRepository;
import com.dudev.orderservice.repository.UserRepository;
import com.dudev.orderservice.service.AuthenticationService;
import com.dudev.orderservice.service.RefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@AutoConfigureMockMvc
public class AuthControllerIT extends IntegrationTestBase {

    private static final String REST_URL = "/api/auth";
    private static final String USERNAME = "testUsername";
    private static final String PASSWORD = "testPassword";


    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Should create user and return tokens")
    void signUp_shouldCreteUser() throws Exception {
        SignUpRequest request = new SignUpRequest(USERNAME, PASSWORD);

        mockMvc.perform(post(REST_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpectAll(jsonPath("$.refreshToken").exists(),
                        jsonPath("$.accessToken").exists(),
                        jsonPath("$.refreshToken").isNotEmpty(),
                        jsonPath("$.accessToken").isNotEmpty());

        User savedUser = userRepository.getUserByUsername(USERNAME).orElse(null);
        Optional<RefreshToken> savedToken = refreshTokenRepository.findByUserId(savedUser.getId());

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getPassword()).isNotEqualTo(PASSWORD);
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
        assertThat(savedToken).isNotEmpty();
    }

    @Test
    @DisplayName("Should authenticate user, create tokens")
    void signIn_shouldLoginWhenCorrectCredentials() throws Exception {
        authenticationService.signUp(new SignUpRequest(USERNAME, PASSWORD));

        SignInRequest signInRequest = new SignInRequest(USERNAME, PASSWORD);

        MvcResult result = mockMvc.perform(post(REST_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andExpectAll(jsonPath("$.refreshToken").exists(),
                        jsonPath("$.accessToken").exists(),
                        jsonPath("$.refreshToken").isNotEmpty(),
                        jsonPath("$.accessToken").isNotEmpty())
                .andReturn();

        JwtAuthenticationResponse actualResponse = objectMapper.createParser(result.getResponse()
                .getContentAsString()).readValueAs(JwtAuthenticationResponse.class);
//        UserDetails authenticatedUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        MvcResult authenticatedUser = mockMvc.perform(get(REST_URL + "/me")
                        .header("Authorization", "Bearer " + actualResponse.getAccessToken()))
                .andExpect(status().isOk())
                .andReturn();

        UserDtoFull userDtoFull = objectMapper.createParser(
                        authenticatedUser
                                .getResponse()
                                .getContentAsString())
                .readValueAs(UserDtoFull.class);

        String token = refreshTokenRepository.findByToken(actualResponse.getRefreshToken()).orElseThrow().getToken();

        assertThat(userDtoFull.getUsername()).isEqualTo(USERNAME);
//        assertThat(authenticatedUser.getUsername()).isNotNull().isEqualTo(USERNAME);
        assertThat(token).isNotNull();
    }

    @Test
    @DisplayName("Should refresh access and refresh token via refresh token")
    void refreshToken_shouldReturnToken() throws Exception {
        User user = userRepository.save(User.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .role(Role.USER)
                .build());
        RefreshToken oldToken = refreshTokenService.create(user.getId());
        RefreshAccessDto refreshAccessDto = RefreshAccessDto.builder()
                .refreshToken(oldToken.getToken()).build();

        MvcResult mvcResult = mockMvc.perform(post(REST_URL + "/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshAccessDto)))
                .andExpectAll(jsonPath("$.refreshToken").exists(),
                        jsonPath("$.accessToken").exists(),
                        jsonPath("$.refreshToken").isNotEmpty(),
                        jsonPath("$.accessToken").isNotEmpty())
                .andReturn();

        JwtAuthenticationResponse actualResponse = objectMapper.createParser(mvcResult.getResponse()
                .getContentAsString()).readValueAs(JwtAuthenticationResponse.class);
        RefreshToken token = refreshTokenRepository.findByToken(actualResponse.getRefreshToken()).orElseThrow();

        assertThat(token).isNotNull().isNotEqualTo(oldToken);
    }

    @Test
    @WithMockUser(username = "testUsername", password = "testPassword")
    void getCurrentUserDetails_shouldReturnCurrentUserDetailsWhenAuthorized() throws Exception {
        User user = userRepository.save(User.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .role(Role.USER)
                .build());

        MvcResult mvcResult = mockMvc.perform(get(REST_URL + "/me"))
                .andExpectAll(status().isOk())
                .andReturn();

        UserDtoFull userDto = objectMapper.createParser(mvcResult.getResponse()
                .getContentAsString()).readValueAs(UserDtoFull.class);

        assertThat(userDto.getUsername()).isEqualTo(USERNAME);
        assertThat(userDto.getId()).isEqualTo(user.getId());
        assertThat(userDto.getRole()).isEqualTo("ROLE_" +   user.getRole().name());
    }
}
