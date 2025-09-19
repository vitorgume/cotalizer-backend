package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false",
                "openia.api.key=TEST_OPENAI_KEY",
                "security.api.key=TEST_SIGNATURES_KEY",
                "secret.key=5a6bf2660e4a4fb7ec956e43959e4e6f826a9662a1f4578bcab89e3178770615",
                "cotalizer.email.avaliacao=EMAIL_TESTE",
                "app.storage.s3.bucket=s3_teste",
                "app.storage.s3.region=teste",
                "app.files.public-base-url=teste",
                "cotalizer.url.alteracao-email=EMAIL_TESTE",
                "api.assinatura.url=url_teste",
                "google.redirect.menu.url=teste",
                "google.redirect.login.url=test",
                "app.security.csrf.secure=false",
                "app.security.csrf.sameSite=None"
        }
)
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
class CsrfControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Test
    @WithMockUser
    void deveRetornarCsrfTokenComoJson() throws Exception {
        mockMvc.perform(get("/csrf").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.headerName").isNotEmpty())
                .andExpect(jsonPath("$.parameterName").isNotEmpty());
    }

    @Test
    @WithMockUser
    void tokenNaoVazio_eComCamposBasicos() throws Exception {
        var mvcResult = mockMvc.perform(get("/csrf").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.headerName", Matchers.not(Matchers.blankString())))
                .andExpect(jsonPath("$.parameterName", Matchers.not(Matchers.blankString())))
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        Map<String, Object> map = objectMapper.readValue(json, new TypeReference<>() {});
        String token = String.valueOf(map.get("token"));
        assertFalse(token == null || token.isBlank(), "token CSRF não deve ser vazio");
    }
}