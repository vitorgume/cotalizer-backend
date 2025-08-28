package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gumeinteligenciacomercial.orcaja.application.gateway.AssinaturaGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.AssinaturaUseCase;
import com.gumeinteligenciacomercial.orcaja.application.usecase.dto.AssinaturaDto;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.UsuarioEntity;
import com.gumeinteligenciacomercial.orcaja.infrastructure.security.jwt.JwtAuthFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false",
                "openia.api.key=TEST_OPENAI_KEY",
                "security.api.key=TEST_SIGNATURES_KEY",
                "secret.key=5a6bf2660e4a4fb7ec956e43959e4e6f826a9662a1f4578bcab89e3178770615"
        }
)
@AutoConfigureMockMvc(addFilters = false)
class AssinaturaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AssinaturaGateway assinaturaGateway;

    private AssinaturaDto assinaturaDto;

    @BeforeEach
    void setUp() {
        assinaturaDto = AssinaturaDto.builder()
                .paymentMethodId("paymenteteste")
                .customerEmail("customeremailteste")
                .idUsuario("id-usuario-teste")
                .build();
    }

    @Test
    void deveCriarAssinaturaComSucesso() throws Exception {

        doNothing().when(assinaturaGateway).enviarNovaAssinatura(any());

        mockMvc.perform(post("/assinaturas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assinaturaDto)))
                .andExpect(status().isOk());

        verify(assinaturaGateway).enviarNovaAssinatura(any());
    }

    @Test
    void deveCancelarComSucesso() throws Exception {

        doNothing().when(assinaturaGateway).enviarCancelamento(any());

        mockMvc.perform(delete("/assinaturas/idUsuarioTeste" )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(assinaturaGateway).enviarCancelamento(any());
    }
}