package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SignatureException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthTokenDataProviderTest {

    private JwtAuthTokenDataProvider newProvider(
            String secret,
            String issuer,
            int accessMinutes,
            int refreshDays,
            long clockSkewSeconds
    ) {
        return new JwtAuthTokenDataProvider(secret, "", issuer, accessMinutes, refreshDays, clockSkewSeconds);
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
        assertThrows(DataProviderException.class, () -> provB.parse(jwtAssinadoComA));
    }

    @Test
    void parse_deveFalhar_quandoIssuerIncorreto() {
        String secret = rawSecret();
        var provIssuerCorreto = newProvider(secret, "issuer-correto", 15, 30, 60);
        var provIssuerErrado  = newProvider(secret, "issuer-errado", 15, 30, 60);

        String jwtComIssuerErrado = provIssuerErrado.generateAccessToken("eve@example.com", "id-2", null);
        assertThrows(DataProviderException.class, () -> provIssuerCorreto.parse(jwtComIssuerErrado));
    }

    @Test
    void parse_deveFalhar_quandoTokenExpirado() {
        var provider = newProvider(rawSecret(), "orcaja-test", -5, 30, 0); // access expirado

        String jwtExpirado = provider.generateAccessToken("frank@example.com", "id-3", null);
        assertThrows(DataProviderException.class, () -> provider.parse(jwtExpirado));
    }

    @Test
    void parse_deveFalhar_quandoTokenMalformado() {
        var provider = newProvider(rawSecret(), "orcaja-test", 15, 30, 60);

        assertThrows(DataProviderException.class, () -> provider.parse("nao-e-um-jwt"));
        assertThrows(DataProviderException.class, () -> provider.parse("a.b"));
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

// === 1) CONSTRUTOR: formatos de segredo ===

    @Test
    void construtor_BASE64_funciona() {
        // chave >= 32 bytes reais
        byte[] raw = "this-is-≥32-bytes-secret-for-hs256-OK-012345".getBytes(StandardCharsets.UTF_8);
        String b64 = Base64.getEncoder().encodeToString(raw);

        var provider = new JwtAuthTokenDataProvider(
                b64, "BASE64", "issuer-b64", 5, 1, 60
        );

        String jwt = provider.generateAccessToken("gina@example.com", "id-4", List.of("USER"));
        var p = provider.parse(jwt);
        assertEquals("gina@example.com", p.subjectEmail());
        assertEquals("id-4", p.userId());
        assertEquals("access", p.type());
        assertEquals(List.of("USER"), p.roles());
    }

    @Test
    void construtor_BASE64URL_funciona() {
        // monta base64url SEM padding
        byte[] raw = "this-is-also-long-enough-for-hs256-base64url".getBytes(StandardCharsets.UTF_8);
        String b64url = Base64.getUrlEncoder().withoutPadding().encodeToString(raw);

        var provider = new JwtAuthTokenDataProvider(
                b64url, "BASE64URL", "issuer-b64url", 5, 1, 60
        );

        String jwt = provider.generateAccessToken("ivy@example.com", "id-5", List.of());
        var p = provider.parse(jwt);
        assertEquals("ivy@example.com", p.subjectEmail());
        assertEquals("id-5", p.userId());
        assertEquals("access", p.type());
        assertTrue(p.roles().isEmpty());
    }

    @Test
    void construtor_HEX_funciona() {
        // 32 bytes em HEX (64 hex chars)
        String hex = "0123456789abcdeffedcba98765432100123456789abcdeffedcba9876543210";

        var provider = new JwtAuthTokenDataProvider(
                hex, "HEX", "issuer-hex", 5, 1, 60
        );

        String jwt = provider.generateAccessToken("henry@example.com", "id-6", List.of("A"));
        var p = provider.parse(jwt);
        assertEquals("henry@example.com", p.subjectEmail());
        assertEquals("id-6", p.userId());
        assertEquals("access", p.type());
        assertEquals(List.of("A"), p.roles());
    }

// === 2) Caminhos de erro em generateAccessToken/Refresh ===

    @Test
    void generateAccessToken_quandoFalhaAssinatura_lancaDataProviderException() throws Exception {
        var provider = newProvider(rawSecret(), "issuer-x", 5, 1, 60);
        // força uma NPE durante sign/compact, removendo a chave
        setPrivateKey(provider, null);

        DataProviderException ex = assertThrows(DataProviderException.class, () ->
                provider.generateAccessToken("a@x", "u", List.of("R"))
        );
        assertEquals("Erro ao gerar o acess token.", ex.getMessage());
    }

    @Test
    void generateRefreshToken_quandoFalhaAssinatura_lancaDataProviderException() throws Exception {
        var provider = newProvider(rawSecret(), "issuer-y", 5, 1, 60);
        setPrivateKey(provider, null);

        DataProviderException ex = assertThrows(DataProviderException.class, () ->
                provider.generateRefreshToken("b@x", "u2")
        );
        assertEquals("Erro ao gerar o refresh token.", ex.getMessage());
    }

// === 3) parse com roles NÃO-lista (ex.: String) -> cai no else e devolve lista vazia ===

    @Test
    void parse_quandoClaimRolesNaoEhLista_retornaRolesVazias() throws Exception {
        var provider = newProvider(rawSecret(), "issuer-r", 10, 10, 60);
        Key key = getPrivateKey(provider);

        Instant now = Instant.now();
        String jwt = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("issuer-r")
                .setSubject("kate@example.com")
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(600)))
                .claim("typ", "access")
                .claim("userId", "id-7")
                .claim("roles", "ADMIN") // <- NÃO é lista
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        var p = provider.parse(jwt);
        assertEquals("kate@example.com", p.subjectEmail());
        assertEquals("id-7", p.userId());
        assertEquals("access", p.type());
        assertTrue(p.roles().isEmpty(), "Quando roles não é List, deve vir vazio");
    }

    private static void setPrivateKey(JwtAuthTokenDataProvider p, Key newKey) throws Exception {
        Field f = JwtAuthTokenDataProvider.class.getDeclaredField("key");
        f.setAccessible(true);
        f.set(p, newKey);
    }
    private static Key getPrivateKey(JwtAuthTokenDataProvider p) throws Exception {
        Field f = JwtAuthTokenDataProvider.class.getDeclaredField("key");
        f.setAccessible(true);
        return (Key) f.get(p);
    }
}