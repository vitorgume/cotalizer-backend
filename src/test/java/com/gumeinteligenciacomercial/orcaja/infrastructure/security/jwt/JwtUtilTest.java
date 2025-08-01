package com.gumeinteligenciacomercial.orcaja.infrastructure.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.jsonwebtoken.JwtException;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    private static final String SECRET_KEY = "01234567890123456789012345678901";
    private JwtUtil jwtUtil;
    private final String email  = "user@example.com";
    private final String userId = "123";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET_KEY);
    }

    @Test
    void gerarTokenDeveRetornarTokenNaoNuloEComTresPartes() {
        String token = jwtUtil.generateToken(email, userId);
        assertNotNull(token, "Token não deve ser nulo");
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT deve ter três partes separadas por ponto");
    }

    @Test
    void extrairUsernameDeveRetornarEmailDoToken() {
        String token = jwtUtil.generateToken(email, userId);
        String extracted = jwtUtil.extractUsername(token);
        assertEquals(email, extracted, "extractUsername deve retornar o assunto (email)");
    }

    @Test
    void extrairUsernameDeveLancarJwtExceptionParaTokenMalformado() {
        String badToken = "este.eh.um.token.invalido";
        assertThrows(JwtException.class,
                () -> jwtUtil.extractUsername(badToken),
                "extractUsername deve lançar JwtException para token malformado"
        );
    }

    @Test
    void tokenEhValidoDeveRetornarVerdadeiroParaTokenValido() {
        String token = jwtUtil.generateToken(email, userId);
        assertTrue(jwtUtil.isTokenValid(token), "isTokenValid deve ser true para token recém-gerado");
    }

    @Test
    void tokenEhValidoDeveRetornarFalsoParaTokenInvalido() {
        assertFalse(jwtUtil.isTokenValid("qualquer-coisa"),
                "isTokenValid deve ser false para string que não é JWT");
    }

    @Test
    void tokenEhValidoDeveRetornarFalsoQuandoTokenForAlterado() {
        String token = jwtUtil.generateToken(email, userId);
        String tampered = token.substring(0, token.length()-1) +
                (token.charAt(token.length()-1) == 'a' ? 'b' : 'a');
        assertFalse(jwtUtil.isTokenValid(tampered),
                "isTokenValid deve ser false para token com assinatura inválida");
    }
}