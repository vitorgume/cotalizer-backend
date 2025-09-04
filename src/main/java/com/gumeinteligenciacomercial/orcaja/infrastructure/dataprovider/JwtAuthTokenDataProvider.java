package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.AuthTokenGateway;
import com.gumeinteligenciacomercial.orcaja.infrastructure.exceptions.DataProviderException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@Slf4j
public class JwtAuthTokenDataProvider implements AuthTokenGateway {

    private final Key key;
    private final String issuer;
    private final int accessMinutes;
    private final int refreshDays;
    private final long clockSkewSeconds;
    private final String MENSAGEM_ERRO_PARSE_JWT = "Erro ao fazer o parse do JWT";
    private final String MENSAGEM_ERRO_GERAR_REFRESH_TOKEN = "Erro ao gerar o refresh token.";
    private final String MENSAGEM_ERRO_GERAR_ACESS_TOKEN = "Erro ao gerar o acess token.";

    public JwtAuthTokenDataProvider(
            @Value("${secret.key}") String secret,
            @Value("${secret.key.format}") String fmt,   // BASE64 | HEX | RAW
            @Value("${app.jwt.issuer:orcaja}") String issuer,
            @Value("${app.jwt.access_minutes:15}") int accessMinutes,
            @Value("${app.jwt.refresh_days:30}") int refreshDays,
            @Value("${app.jwt.clock_skew_seconds:60}") long clockSkewSeconds
    ) {
        secret = secret.trim();
        byte[] keyBytes = switch (fmt) {
            case "HEX" -> HexFormat.of().parseHex(secret);                      // sua chave atual em HEX
            case "BASE64" -> io.jsonwebtoken.io.Decoders.BASE64.decode(secret);
            case "BASE64URL" -> io.jsonwebtoken.io.Decoders.BASE64URL.decode(secret);
            default -> secret.getBytes(StandardCharsets.UTF_8);              // RAW (evite em prod)
        };

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.issuer = issuer;
        this.accessMinutes = accessMinutes;
        this.refreshDays = refreshDays;
        this.clockSkewSeconds = clockSkewSeconds;
    }

    @Override
    public String generateAccessToken(String subjectEmail, String userId, Collection<String> roles) {
        Instant now = Instant.now();
        try {
            return Jwts.builder()
                    .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                    .setIssuer(issuer)
                    .setSubject(subjectEmail)
                    .setId(UUID.randomUUID().toString())
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(now.plus(accessMinutes, ChronoUnit.MINUTES)))
                    .claim("typ", "access")
                    .claim("userId", userId)
                    .claim("roles", roles == null ? List.of() : new ArrayList<>(roles))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_GERAR_ACESS_TOKEN, ex);
            throw new DataProviderException(MENSAGEM_ERRO_GERAR_ACESS_TOKEN, ex.getCause());
        }
    }

    @Override
    public String generateRefreshToken(String subjectEmail, String userId) {
        Instant now = Instant.now();

        try {
            return Jwts.builder()
                    .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                    .setIssuer(issuer)
                    .setSubject(subjectEmail)
                    .setId(UUID.randomUUID().toString()) // jti p/ rotação/blacklist se quiser
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(now.plus(refreshDays, ChronoUnit.DAYS)))
                    .claim("typ", "refresh")
                    .claim("userId", userId)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_GERAR_REFRESH_TOKEN, ex);
            throw new DataProviderException(MENSAGEM_ERRO_GERAR_REFRESH_TOKEN, ex.getCause());
        }
    }

    @Override
    public ParsedToken parse(String jwt) {
        Jws<Claims> jws = null;
        String subj = "";
        String uid = "";
        String typ = "";
        Object rawRoles = null;

        try {
            jws = Jwts.parserBuilder()
                    .requireIssuer(issuer)
                    .setAllowedClockSkewSeconds(clockSkewSeconds)
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt);


            Claims c = jws.getBody();
            subj = c.getSubject();
            uid = c.get("userId", String.class);
            typ = c.get("typ", String.class);
            rawRoles = c.get("roles");
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_PARSE_JWT, ex);
            throw new DataProviderException(MENSAGEM_ERRO_PARSE_JWT, ex.getCause());
        }
        List<String> roles = new ArrayList<>();
        if (rawRoles instanceof List<?> l) l.forEach(o -> roles.add(String.valueOf(o)));
        return new ParsedToken(subj, uid, typ, roles);
    }
}
