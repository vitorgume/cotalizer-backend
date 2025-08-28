package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CriptografiaDataProviderTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CriptografiaDataProvider provider;

    @Test
    void criptografarDeveDelegarParaPasswordEncoderEncode() {
        String raw = "minhaSenha";
        String encoded = "$2a$10$abcdef...";

        given(passwordEncoder.encode(raw)).willReturn(encoded);

        String resultado = provider.criptografar(raw);

        assertEquals(encoded, resultado, "Deve retornar o valor que o PasswordEncoder.encode() produzir");
        then(passwordEncoder).should(times(1)).encode(raw);
    }

    @Test
    void validarSenhaDeveRetornarTrueQuandoPasswordEncoderMatchesRetornaTrue() {
        String raw = "senha";
        String hash = "$2a$10$hashaqui";

        given(passwordEncoder.matches(raw, hash)).willReturn(true);

        boolean valido = provider.validarSenha(raw, hash);

        assertTrue(valido, "Deve retornar true quando PasswordEncoder.matches() for true");
        then(passwordEncoder).should(times(1)).matches(raw, hash);
    }

    @Test
    void validarSenhaDeveRetornarFalseQuandoPasswordEncoderMatchesRetornaFalse() {
        String raw = "senhaErrada";
        String hash = "$2a$10$outrohash";

        given(passwordEncoder.matches(raw, hash)).willReturn(false);

        boolean valido = provider.validarSenha(raw, hash);

        assertFalse(valido, "Deve retornar false quando PasswordEncoder.matches() for false");
        then(passwordEncoder).should(times(1)).matches(raw, hash);
    }
}