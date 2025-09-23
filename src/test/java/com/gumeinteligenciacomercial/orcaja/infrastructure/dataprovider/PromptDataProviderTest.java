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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
    void buscarPorIdAtivoComSucessoDeveRetornarListaDomain() {
        given(repository.findByAtivoTrue()).willReturn(List.of(entity1, entity2));

        try (MockedStatic<PromptMapper> ms = mockStatic(PromptMapper.class)) {
            ms.when(() -> PromptMapper.paraDomain(entity1)).thenReturn(domain1);
            ms.when(() -> PromptMapper.paraDomain(entity2)).thenReturn(domain2);

            List<Prompt> resultados = provider.buscarPorIdAtivo();

            assertEquals(2, resultados.size());
            assertSame(domain1, resultados.get(0));
            assertSame(domain2, resultados.get(1));

            then(repository).should().findByAtivoTrue();
            ms.verify(() -> PromptMapper.paraDomain(entity1));
            ms.verify(() -> PromptMapper.paraDomain(entity2));
        }
    }

    @Test
    void buscarPorIdAtivoQuandoRepositoryLancarErroDeveLancarDataProviderException() {
        given(repository.findByAtivoTrue()).willThrow(new RuntimeException("erro remoto"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.buscarPorIdAtivo()
        );
        assertEquals(ERR_BUSCAR, ex.getMessage());
        then(repository).should().findByAtivoTrue();
    }
}