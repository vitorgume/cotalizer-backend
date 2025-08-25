package com.gumeinteligenciacomercial.orcaja.infrastructure.dataprovider;

import com.gumeinteligenciacomercial.orcaja.application.gateway.AuthTokenGateway;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class JwtAuthTokenDataProvider implements AuthTokenGateway {

    private final Key key;
    private final String issuer;
    private final int accessMinutes;
    private final int refreshDays;
    private final long clockSkewSeconds;

    public JwtAuthTokenDataProvider(
            @Value("${secret.key}") String secretBase64,
            @Value("${app.jwt.issuer:orcaja}") String issuer,
            @Value("${app.jwt.access_minutes:15}") int accessMinutes,
            @Value("${app.jwt.refresh_days:30}") int refreshDays,
            @Value("${app.jwt.clock_skew_seconds:60}") long clockSkewSeconds
    ) {
        byte[] keyBytes;
        try { keyBytes = Decoders.BASE64.decode(secretBase64); }
        catch (IllegalArgumentException e) { keyBytes = secretBase64.getBytes(); }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.issuer = issuer;
        this.accessMinutes = accessMinutes;
        this.refreshDays = refreshDays;
        this.clockSkewSeconds = clockSkewSeconds;
    }

    @Override
    public String generateAccessToken(String subjectEmail, String userId, Collection<String> roles) {
        Instant now = Instant.now();
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
    }

    @Override
    public String generateRefreshToken(String subjectEmail, String userId) {
        Instant now = Instant.now();
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
    }

    @Override
    public ParsedToken parse(String jwt) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .requireIssuer(issuer)
                .setAllowedClockSkewSeconds(clockSkewSeconds)
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt);

        Claims c = jws.getBody();
        String subj = c.getSubject();
        String uid  = c.get("userId", String.class);
        String typ  = c.get("typ", String.class);
        Object rawRoles = c.get("roles");
        List<String> roles = new ArrayList<>();
        if (rawRoles instanceof List<?> l) l.forEach(o -> roles.add(String.valueOf(o)));
        return new ParsedToken(subj, uid, typ, roles);
    }
}
