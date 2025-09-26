package com.gumeinteligenciacomercial.orcaja.entrypoint.controller;

import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.PlanoRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.PlanoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false",
                "openia.api.key=TEST_OPENAI_KEY",
                "security.api.key=TEST_SIGNATURES_KEY",
                "secret.key=5a6bf2660e4a4fb7ec956e43959e4e6f826a9662a1f4578bcab89e3178770615"
        }
)
@AutoConfigureMockMvc(addFilters = false)
class PlanoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlanoRepository repository;

    List<PlanoEntity> planoEntityList;

    @BeforeEach
    void setUp() {
         planoEntityList = List.of(
                PlanoEntity.builder()
                        .id("idteste123")
                        .titulo("p1")
                        .build(),
                PlanoEntity.builder()
                        .id("idteste321")
                        .titulo("p2")
                        .build()
        );
    }

    @Test
    void deveListarPlanosComSucesso() throws Exception {

        Mockito.when(repository.findAll()).thenReturn(planoEntityList);

        mockMvc.perform(get("/planos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.[0].id").value(planoEntityList.get(0).getId()))
                .andExpect(jsonPath("$.dado.[1].id").value(planoEntityList.get(1).getId()));

        Mockito.verify(repository).findAll();
    }
}