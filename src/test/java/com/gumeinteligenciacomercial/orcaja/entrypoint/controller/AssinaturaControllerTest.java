package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gumeinteligenciacomercial.orcaja.application.usecase.AssinaturaUseCase;
import com.gumeinteligenciacomercial.orcaja.application.usecase.dto.AssinaturaDto;
import com.gumeinteligenciacomercial.orcaja.infrastructure.security.jwt.JwtAuthFilter;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AssinaturaController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        },
        excludeFilters = @ComponentScan.Filter(
                type  = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AssinaturaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock do UseCase, igual ao @MockitoBean do seu exemplo
    @MockitoBean
    private AssinaturaUseCase assinaturaUseCase;

    @Test
    void criarNovaAssinatura_deveRetornarOkEChamarUseCase() throws Exception {
        // --- dado
        AssinaturaDto dto = AssinaturaDto.builder()
                .idUsuario("userId123")
                .paymentMethodId("pm_123")
                .customerEmail("user@example.com")
                .build();

        // --- quando / então
        mockMvc.perform(post("/assinaturas")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));  // corpo vazio

        // --- verifica que o UseCase foi chamado com o DTO correto
        ArgumentCaptor<AssinaturaDto> captor = ArgumentCaptor.forClass(AssinaturaDto.class);
        verify(assinaturaUseCase).criarAssinatura(captor.capture());
        AssinaturaDto capturado = captor.getValue();
        assertThat(capturado.getIdUsuario()).isEqualTo(dto.getIdUsuario());
        assertThat(capturado.getPaymentMethodId()).isEqualTo(dto.getPaymentMethodId());
        assertThat(capturado.getCustomerEmail()).isEqualTo(dto.getCustomerEmail());
    }

    @Test
    void cancelarAssinatura_deveRetornarNoContentEChamarUseCase() throws Exception {
        String userId = "userId123";

        mockMvc.perform(delete("/assinaturas/{idUsuario}", userId))
                .andExpect(status().isNoContent());

        verify(assinaturaUseCase).cancelar(userId);
    }
}