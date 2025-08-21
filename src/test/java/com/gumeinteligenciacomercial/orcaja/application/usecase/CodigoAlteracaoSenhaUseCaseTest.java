package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.CodigoInvalidoAlteracaoSenha;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodigoAlteracaoSenhaUseCaseTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    @InjectMocks
    private CodigoAlteracaoSenhaUseCase useCase;

    private final String usuarioId = "user123";
    private final String codigo    = "abcde";

    @Test
    void adicionarAoCacheDeveSetarValorComTTL() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        useCase.adicionarAoCache(usuarioId, codigo);

        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOps, times(1))
                .set("codigo:" + codigo, usuarioId, 30, java.util.concurrent.TimeUnit.MINUTES);
    }

    @Test
    void validaCodigoAlteracaoSenhaQuandoCodigoValidoRetornarUsuario() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("codigo:" + codigo)).thenReturn(usuarioId);

        String result = useCase.validaCodigoAlteracaoSenha(codigo);

        assertEquals(usuarioId, result);
        verify(valueOps, times(1)).get("codigo:" + codigo);
    }

    @Test
    void validaCodigoAlteracaoSenhaQuandoCodigoInvalidoLancarExcecao() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("codigo:" + codigo)).thenReturn(null);

        assertThrows(CodigoInvalidoAlteracaoSenha.class, () -> useCase.validaCodigoAlteracaoSenha(codigo));
        verify(valueOps, times(1)).get("codigo:" + codigo);
    }
}