package com.vmsac.vmsacserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@code /api/auth} endpoints.
 *
 * <p>Uses {@code @SpringBootTest} + {@code MockMvc} against the full Spring context
 * backed by the H2 in-memory test database (profile "test").  Each test runs in a
 * transaction rolled back on completion, keeping the DB clean between tests.
 *
 * <p>Roles are seeded once by {@code VmsAcServerApplication.initRoles()} at context
 * startup — no {@code @Sql} is needed.  Adding a redundant {@code @Sql} INSERT here
 * would cause {@code NonUniqueResultException} on role lookups during signup.
 *
 * <p>Covers:
 * <ul>
 *   <li>POST /api/auth/signup  — 200 on success; 400 on duplicate email</li>
 *   <li>POST /api/auth/signin  — 200 + JWT on valid credentials; 4xx on wrong password</li>
 *   <li>POST /api/auth/refreshtoken — valid refresh token returns new access token</li>
 *   <li>GET  /api/persons (non-localhost) — no token → 401 Unauthorized</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String EMAIL = "integration@test.com";
    private static final String PASSWORD = "password123";

    @Test
    void signup_validRequest_returns200WithMessage() throws Exception {
        String body = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\",\"firstName\":\"Test\",\"lastName\":\"User\",\"mobile\":\"1234567890\"}",
                EMAIL, PASSWORD);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    void signup_duplicateEmail_returns400() throws Exception {
        performSignup(EMAIL, PASSWORD).andExpect(status().isOk());
        performSignup(EMAIL, PASSWORD).andExpect(status().isBadRequest());
    }

    @Test
    void signin_validCredentials_returns200WithJwt() throws Exception {
        performSignup(EMAIL, PASSWORD);

        String loginBody = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}", EMAIL, PASSWORD);

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.email").value(EMAIL));
    }

    @Test
    void signin_wrongPassword_returns4xx() throws Exception {
        performSignup(EMAIL, PASSWORD);

        String loginBody = String.format(
                "{\"email\":\"%s\",\"password\":\"wrongpassword\"}", EMAIL);

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void refreshToken_validRefreshToken_returnsNewAccessToken() throws Exception {
        performSignup(EMAIL, PASSWORD);

        // Sign in and capture refresh token
        MvcResult signinResult = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format(
                                "{\"email\":\"%s\",\"password\":\"%s\"}", EMAIL, PASSWORD)))
                .andReturn();

        Map<?, ?> signinBody = mapper.readValue(
                signinResult.getResponse().getContentAsString(), Map.class);
        String refreshToken = (String) signinBody.get("refreshToken");

        mockMvc.perform(post("/api/auth/refreshtoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"refreshToken\":\"%s\"}", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void protectedEndpoint_noToken_returns401() throws Exception {
        // Security config permits 127.0.0.1 without auth; simulate a non-localhost request
        mockMvc.perform(get("/api/persons")
                        .with(request -> { request.setRemoteAddr("10.0.0.1"); return request; }))
                .andExpect(status().isUnauthorized());
    }

    // ---- helpers ----

    private ResultActions performSignup(String email, String password) throws Exception {
        String body = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\",\"firstName\":\"Test\",\"lastName\":\"User\",\"mobile\":\"1234567890\"}",
                email, password);
        return mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }
}
