package com.gumeinteligenciacomercial.orcaja.application.usecase.ia;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.ErroEnviarParaIaException;
import com.gumeinteligenciacomercial.orcaja.application.gateway.IaGateway;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.dto.OpenIaResponseDto;
import com.gumeinteligenciacomercial.orcaja.application.usecase.ia.dto.PromptDto;
import com.gumeinteligenciacomercial.orcaja.domain.Prompt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IaUseCaseTest {

    @Mock
    private PromptUseCase promptUseCase;

    @Mock
    private IaGateway gateway;

    private IaUseCase iaUseCase;

    @Captor
    private ArgumentCaptor<PromptDto> promptDtoCaptor;

    private static final String GERADOR_ID = "app.id.prompt.ia.gerador-orcamento";
    private static final String INTERP_ID  = "app.id.prompt.ia.interpretador-prompt";

    private static final String SYS_GERADOR = "SYS-GERADOR";
    private static final String SYS_INTERP  = "SYS-INTERP";

    @BeforeEach
    void setup() {
        iaUseCase = new IaUseCase(promptUseCase, gateway, GERADOR_ID, INTERP_ID);
    }

    private OpenIaResponseDto mockResponseWithContent(String content) {
        OpenIaResponseDto responseIa = mock(OpenIaResponseDto.class);
        OpenIaResponseDto.Choice choice = mock(OpenIaResponseDto.Choice.class);
        OpenIaResponseDto.Message message = mock(OpenIaResponseDto.Message.class);
        LinkedList<OpenIaResponseDto.Choice> choices = new LinkedList<>();
        choices.add(choice);
        when(responseIa.getChoices()).thenReturn(choices);
        when(choice.getMessage()).thenReturn(message);
        when(message.getContent()).thenReturn(content);
        return responseIa;
    }

    private Prompt prompt(String conteudo, String model) {
        return Prompt.builder()
                .id("any-id")
                .conteudo(conteudo)
                .modelIa(model)
                .build();
    }

    @Test
    void gerarOrcamento_deveChamarGatewayComPromptCorretoERetornarMapa() {
        String conteudoOriginal = "Quero 3 canetas azuis";
        when(promptUseCase.buscarPorIdAtivo(GERADOR_ID))
                .thenReturn(prompt(SYS_GERADOR, "gpt-4o-mini"));
        when(gateway.enviarMensagem(any(PromptDto.class)))
                .thenAnswer(invocation -> {
                    PromptDto dto = invocation.getArgument(0);
                    // validação básica inline: system + user
                    assertEquals("gpt-4o-mini", dto.getModel());
                    assertEquals(2, dto.getMessages().size());
                    assertEquals("system", dto.getMessages().get(0).getRole());
                    assertEquals(SYS_GERADOR, dto.getMessages().get(0).getContent());
                    assertEquals("user", dto.getMessages().get(1).getRole());
                    assertEquals(conteudoOriginal, dto.getMessages().get(1).getContent());
                    return mockResponseWithContent("""
                      {
                        "produto": "Caneta",
                        "quantidade": 3
                      }
                    """);
                });

        Map<String, Object> resultado = iaUseCase.gerarOrcamento(conteudoOriginal);

        verify(promptUseCase, times(1)).buscarPorIdAtivo(GERADOR_ID);
        verify(gateway, times(1)).enviarMensagem(promptDtoCaptor.capture());

        PromptDto usado = promptDtoCaptor.getValue();
        assertEquals("gpt-4o-mini", usado.getModel());
        assertEquals("system", usado.getMessages().get(0).getRole());
        assertEquals(SYS_GERADOR, usado.getMessages().get(0).getContent());
        assertEquals("user", usado.getMessages().get(1).getRole());
        assertEquals(conteudoOriginal, usado.getMessages().get(1).getContent());

        assertEquals("Caneta", resultado.get("produto"));
        assertEquals(3, ((Number) resultado.get("quantidade")).intValue());
    }

    @Test
    void quandoPrimariaFalhaComJsonInvalido_deveFazerRetryEAposFallbackTerSucesso() {
        // arrange
        when(promptUseCase.buscarPorIdAtivo(GERADOR_ID))
                .thenReturn(prompt(SYS_GERADOR, "gpt-4o"));
        when(promptUseCase.buscarPorIdAtivo(INTERP_ID))
                .thenReturn(prompt(SYS_INTERP, "gpt-4o"));

        AtomicInteger primarias = new AtomicInteger(0);

        // gateway.enviarMensagem decide resposta olhando o "system" da mensagem
        when(gateway.enviarMensagem(any(PromptDto.class))).thenAnswer(invocation -> {
            PromptDto dto = invocation.getArgument(0);
            String system = dto.getMessages().get(0).getContent();

            if (SYS_GERADOR.equals(system)) {
                int n = primarias.incrementAndGet();

                if (n <= 4) {
                    return mockResponseWithContent("<<< isto não é JSON >>>");
                }

                return mockResponseWithContent("""
                  { "ok": true, "valor": 123 }
                """);
            } else if (SYS_INTERP.equals(system)) {
                return mockResponseWithContent("TEXTO_INTERPRETADO_PELA_SECUNDARIA");
            }
            throw new IllegalStateException("System prompt desconhecido");
        });

        Map<String, Object> out = iaUseCase.gerarOrcamento("INPUT ORIGINAL");

        assertEquals(5, primarias.get());

        verify(gateway, atLeast(6)).enviarMensagem(promptDtoCaptor.capture());
        List<PromptDto> todas = promptDtoCaptor.getAllValues();

        PromptDto ultimaPrimaria = null;
        for (int i = todas.size() - 1; i >= 0; i--) {
            if (SYS_GERADOR.equals(todas.get(i).getMessages().get(0).getContent())) {
                ultimaPrimaria = todas.get(i);
                break;
            }
        }
        assertNotNull(ultimaPrimaria);
        assertEquals("user", ultimaPrimaria.getMessages().get(1).getRole());
        assertEquals("TEXTO_INTERPRETADO_PELA_SECUNDARIA",
                ultimaPrimaria.getMessages().get(1).getContent());

        assertEquals(true, out.get("ok"));
        assertEquals(123, ((Number) out.get("valor")).intValue());

        verify(promptUseCase, atLeastOnce()).buscarPorIdAtivo(GERADOR_ID);
        verify(promptUseCase, atLeastOnce()).buscarPorIdAtivo(INTERP_ID);
    }

    @Test
    void quandoPrimariaEFallbackNaoResolvem_deveLancarErroEnviarParaIaException() {
        when(promptUseCase.buscarPorIdAtivo(GERADOR_ID))
                .thenReturn(prompt(SYS_GERADOR, "gpt-4o"));
        when(promptUseCase.buscarPorIdAtivo(INTERP_ID))
                .thenReturn(prompt(SYS_INTERP, "gpt-4o"));

        AtomicInteger primarias = new AtomicInteger(0);
        AtomicInteger secundarias = new AtomicInteger(0);

        when(gateway.enviarMensagem(any(PromptDto.class))).thenAnswer(invocation -> {
            PromptDto dto = invocation.getArgument(0);
            String system = dto.getMessages().get(0).getContent();

            if (SYS_GERADOR.equals(system)) {
                primarias.incrementAndGet();
                return mockResponseWithContent("NÃO_JSON");
            } else if (SYS_INTERP.equals(system)) {
                secundarias.incrementAndGet();
                return mockResponseWithContent("QUALQUER_TEXTO");
            }
            throw new IllegalStateException("System prompt desconhecido");
        });

        assertThrows(ErroEnviarParaIaException.class,
                () -> iaUseCase.gerarOrcamento("input"));

        assertTrue(primarias.get() >= 5, "esperado ao menos 5 chamadas primárias");
        assertEquals(1, secundarias.get(), "esperado 1 chamada secundária");
    }

    @Test
    void quandoGatewayLancaExcecao_deveRetryAteSucesso() {
        when(promptUseCase.buscarPorIdAtivo(GERADOR_ID))
                .thenReturn(prompt(SYS_GERADOR, "gpt-4o"));

        AtomicInteger chamadas = new AtomicInteger(0);

        when(gateway.enviarMensagem(any(PromptDto.class))).thenAnswer(invocation -> {
            int n = chamadas.incrementAndGet();
            if (n <= 2) {
                throw new RuntimeException("Falha de rede");
            }
            return mockResponseWithContent("""
               {"status":"OK","tentativa":3}
            """);
        });

        Map<String, Object> resp = iaUseCase.gerarOrcamento("teste");

        assertEquals("OK", resp.get("status"));
        assertEquals(3, ((Number) resp.get("tentativa")).intValue());

        assertEquals(3, chamadas.get());
        verify(promptUseCase, atLeastOnce()).buscarPorIdAtivo(GERADOR_ID);
        verify(promptUseCase, never()).buscarPorIdAtivo(INTERP_ID); // nem precisou cair no fallback
    }
}