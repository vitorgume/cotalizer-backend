package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthTokenDataProviderTest {

    private JwtAuthTokenDataProvider newProvider(
            String secret,
            String issuer,
            int accessMinutes,
            int refreshDays,
            long clockSkewSeconds
    ) {
        return new JwtAuthTokenDataProvider(secret, issuer, accessMinutes, refreshDays, clockSkewSeconds);
    }

    // Segredo RAW (não-base64) com ≥ 32 bytes
    private String rawSecret() {
        return "5a6bf2660e4a4fb7ec956e43959e4e6f826a9662a1f4578bcab89e3178770615"; // 46+ bytes
    }

    // Segredo em Base64 representando ≥ 32 bytes reais
    private String base64Secret32() {
        byte[] raw = "this-is-≥32-bytes-secret-for-hs256-OK-012345".getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(raw);
    }

    @Test
    void generateAccessToken_semRoles_parseOK_rolesVazias() {
        var provider = newProvider(rawSecret(), "orcaja-test", 15, 30, 60);

        String jwt = provider.generateAccessToken("alice@example.com", "u-1", null);
        assertNotNull(jwt);

        var p = provider.parse(jwt);
        assertEquals("alice@example.com", p.subjectEmail());
        assertEquals("u-1", p.userId());
        assertEquals("access", p.type());
        assertNotNull(p.roles());
        assertTrue(p.roles().isEmpty());
    }

    @Test
    void generateAccessToken_comRoles_parseOK_rolesMantidas() {
        var provider = newProvider(rawSecret(), "orcaja-test", 10, 30, 60);

        String jwt = provider.generateAccessToken("bob@example.com", "u-2", List.of("ADMIN", "USER"));
        var p = provider.parse(jwt);
        assertEquals("bob@example.com", p.subjectEmail());
        assertEquals("u-2", p.userId());
        assertEquals("access", p.type());
        assertEquals(List.of("ADMIN", "USER"), p.roles());
    }

    @Test
    void generateRefreshToken_parseOK_semRoles_tipoRefresh() {
        var provider = newProvider(rawSecret(), "orcaja-test", 15, 7, 60);

        String jwt = provider.generateRefreshToken("carol@example.com", "u-3");
        var p = provider.parse(jwt);
        assertEquals("carol@example.com", p.subjectEmail());
        assertEquals("u-3", p.userId());
        assertEquals("refresh", p.type());
        assertNotNull(p.roles());
        assertTrue(p.roles().isEmpty());
    }

    @Test
    void parse_deveFalhar_quandoAssinaturaInvalida() {
        var provA = newProvider("5a6bf2660e4a4fb7ec956e43959e4e6f826a9662a1f4578bcab89e3178770615", "orcaja-test", 15, 30, 0);
        var provB = newProvider("5b6bf2660e4a4fb7ec956e43959e4e6f826a9662a1f4578bcab89e3178770615", "orcaja-test", 15, 30, 0);

        String jwtAssinadoComA = provA.generateAccessToken("dave@example.com", "id-1", List.of());
        assertThrows(SignatureException.class, () -> provB.parse(jwtAssinadoComA));
    }

    @Test
    void parse_deveFalhar_quandoIssuerIncorreto() {
        String secret = rawSecret();
        var provIssuerCorreto = newProvider(secret, "issuer-correto", 15, 30, 60);
        var provIssuerErrado  = newProvider(secret, "issuer-errado", 15, 30, 60);

        String jwtComIssuerErrado = provIssuerErrado.generateAccessToken("eve@example.com", "id-2", null);
        assertThrows(IncorrectClaimException.class, () -> provIssuerCorreto.parse(jwtComIssuerErrado));
    }

    @Test
    void parse_deveFalhar_quandoTokenExpirado() {
        var provider = newProvider(rawSecret(), "orcaja-test", -5, 30, 0); // access expirado

        String jwtExpirado = provider.generateAccessToken("frank@example.com", "id-3", null);
        assertThrows(ExpiredJwtException.class, () -> provider.parse(jwtExpirado));
    }

    @Test
    void parse_deveFalhar_quandoTokenMalformado() {
        var provider = newProvider(rawSecret(), "orcaja-test", 15, 30, 60);

        assertThrows(MalformedJwtException.class, () -> provider.parse("nao-e-um-jwt"));
        assertThrows(MalformedJwtException.class, () -> provider.parse("a.b"));
    }

    @Test
    void construtor_deveAceitarSecretEmBase64() {
        // Aqui forçamos o caminho Base64 de propósito:
        var provider = newProvider(base64Secret32(), "orcaja-test", 5, 1, 60);

        String jwt = provider.generateAccessToken("gina@example.com", "id-4", List.of("USER"));
        var p = provider.parse(jwt);

        assertEquals("gina@example.com", p.subjectEmail());
        assertEquals("id-4", p.userId());
        assertEquals("access", p.type());
        assertEquals(List.of("USER"), p.roles());
    }
}