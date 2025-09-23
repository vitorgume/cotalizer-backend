package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.domain.Prompt;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import com.gumeinteligenciacomercial.orcaja.infrastructure.mapper.PromptMapper;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.PromptRepository;
import com.gumeinteligenciacomercial.orcaja.infrastructure.repositories.entities.PromptEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class PromptDataProviderTest {
    private static final String ERR_BUSCAR = "Erro ao buscar por prompts ativos.";

    @Mock
    private PromptRepository repository;

    @InjectMocks
    private PromptDataProvider provider;

    private PromptEntity entity1;
    private PromptEntity entity2;
    private Prompt domain1;
    private Prompt domain2;

    @BeforeEach
    void setUp() {
        entity1 = new PromptEntity();
        entity2 = new PromptEntity();
        domain1 = Prompt.builder().id(UUID.randomUUID().toString()).build();
        domain2 = Prompt.builder().id(UUID.randomUUID().toString()).build();
    }

    @Test
    void buscarPorIdAtivoQuandoRepositoryLancarErroDeveLancarDataProviderException() {
        given(repository.findByIdAndAtivoTrue(Mockito.anyString())).willThrow(new RuntimeException("erro remoto"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.buscarPorIdAtivo("")
        );
        assertEquals(ERR_BUSCAR, ex.getMessage());
        then(repository).should().findByIdAndAtivoTrue("");
    }
}