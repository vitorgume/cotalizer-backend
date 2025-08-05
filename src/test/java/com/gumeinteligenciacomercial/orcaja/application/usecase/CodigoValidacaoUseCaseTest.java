package com.gumeinteligenciacomercial.orcaja.application.usecase;

import com.gumeinteligenciacomercial.orcaja.application.exceptions.CodigoInvalidoValidacaoEmailException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodigoValidacaoUseCaseTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    @InjectMocks
    private CodigoValidacaoUseCase useCase;

    @Captor
    private ArgumentCaptor<String> keyCaptor;

    @Captor
    private ArgumentCaptor<String> codeCaptor;

    @Captor
    private ArgumentCaptor<Long> ttlCaptor;

    @Captor
    private ArgumentCaptor<TimeUnit> timeUnitCaptor;

    private final String email = "user@example.com";

    @BeforeEach
    void setup() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    @Test
    void gerarCodigoDeveGerarCodigoSeisDigitosSalvarNoRedisComTTL() {
        String codigo = useCase.gerarCodigo(email);

        assertNotNull(codigo);
        assertTrue(Pattern.matches("\\d{6}", codigo), "Código deve ter exatamente 6 dígitos");

        verify(valueOps, times(1))
                .set(keyCaptor.capture(), codeCaptor.capture(), ttlCaptor.capture(), timeUnitCaptor.capture());

        assertEquals(email, keyCaptor.getValue());
        assertEquals(codigo, codeCaptor.getValue());
        assertEquals(10L, ttlCaptor.getValue());
        assertEquals(TimeUnit.MINUTES, timeUnitCaptor.getValue());
    }

    @Test
    void validarQuandoCodigoCorretoRetornarTrue() {
        String expectedCode = "123456";
        when(valueOps.get(email)).thenReturn(expectedCode);

        boolean result = useCase.validar(email, expectedCode);

        assertTrue(result);
        verify(valueOps, times(1)).get(email);
    }

    @Test
    void validarQuandoCodigoIncorretoDeveLancarExcecao() {
        when(valueOps.get(email)).thenReturn("654321");

        assertThrows(CodigoInvalidoValidacaoEmailException.class,
                () -> useCase.validar(email, "123456"));
        verify(valueOps, times(1)).get(email);
    }

    @Test
    void validarQuandoCodigoNaoExisteDeveLancarExcecao() {
        when(valueOps.get(email)).thenReturn(null);

        assertThrows(CodigoInvalidoValidacaoEmailException.class,
                () -> useCase.validar(email, "000000"));
        verify(valueOps, times(1)).get(email);
    }
}