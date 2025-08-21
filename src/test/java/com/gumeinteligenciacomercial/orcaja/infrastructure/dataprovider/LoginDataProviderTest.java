package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.infrastructure.security.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class LoginDataProviderTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private LoginDataProvider provider;

    @Test
    void generateTokenDeveDelegarParaJwtUtil() {
        String email = "user@example.com";
        String userId = "abc123";
        String fakeToken = "jwt.token.value";
        given(jwtUtil.generateToken(email, userId)).willReturn(fakeToken);

        String token = provider.generateToken(email, userId);

        assertSame(fakeToken, token, "Deve retornar exatamente o que JwtUtil.generateToken devolve");
        then(jwtUtil).should(times(1)).generateToken(email, userId);
    }

    @Test
    void deveLancarException() {
        given(jwtUtil.generateToken(anyString(), anyString()))
                .willThrow(new IllegalStateException("jwt failure"));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> provider.generateToken("x@y.z", "id"),
                "Exceção de JwtUtil deve ser propagada sem captura"
        );
        assertEquals("jwt failure", ex.getMessage());
    }
}