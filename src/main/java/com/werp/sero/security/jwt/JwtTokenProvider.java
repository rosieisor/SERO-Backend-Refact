package com.werp.sero.security.jwt;

import com.werp.sero.security.userdetails.CustomUserDetails;
import com.werp.sero.security.dto.JwtToken;
import com.werp.sero.security.enums.Type;
import com.werp.sero.security.jwt.exception.ExpiredTokenException;
import com.werp.sero.security.jwt.exception.InvalidTokenException;
import com.werp.sero.security.userdetails.JwtUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String ID_KEY = "id";
    private static final String TYPE_KEY = "type";
    private static final String CLIENT_KEY = "client";

    @Value("${jwt.secret-key}")
    private String secretKeyString;

    @Value("${jwt.access-token-expiration-time}")
    private long accessTokenExpirationTime;

    @Value("${jwt.refresh-token-expiration-time}")
    private long refreshTokenExpirationTime;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        final byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtToken generateAccessToken(final Authentication authentication) {
        final CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        final String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        final String accessToken = Jwts.builder()
                .subject(userDetails.getUsername())
                .claim(AUTHORITIES_KEY, authorities)
                .claim(TYPE_KEY, userDetails.getType().name())
                .claim(ID_KEY, userDetails.getId())
                .claim(CLIENT_KEY, userDetails.getClientId())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                .signWith(secretKey)
                .compact();

        return new JwtToken(accessToken, authorities, accessTokenExpirationTime);
    }

    public JwtToken generateRefreshToken(final Authentication authentication) {
        final CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        final String refreshToken = Jwts.builder()
                .subject(customUserDetails.getUsername())
                .claim(ID_KEY, customUserDetails.getId())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                .signWith(secretKey)
                .compact();

        return new JwtToken(refreshToken, null, refreshTokenExpirationTime);
    }

    public Claims parseClaims(final String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(final String token) {
        return parseClaims(token).getSubject();
    }

    public Type extractType(final String token) {
        return Type.valueOf(parseClaims(token).get(TYPE_KEY).toString());
    }

    public Authentication getAuthentication(final String token) {
        final Claims claims = parseClaims(token);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new InvalidTokenException();
        }

        final List<GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        final Type type = extractType(token);

        final int id = ((Number) claims.get(ID_KEY)).intValue();

        final String email = extractEmail(token);

        final Integer clientId = (Integer) claims.get(CLIENT_KEY);

        final List<String> permissionList = Arrays.asList(claims.get(AUTHORITIES_KEY).toString().split(","));

        final JwtUserDetails userDetails = new JwtUserDetails(id, email, clientId, type, permissionList);

        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    public boolean validateToken(final String accessToken) {
        try {
            parseClaims(accessToken);
            return true;
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            log.error("Invalid JWT Token: {}", e.getMessage());
            throw new InvalidTokenException();
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT Token: {}", e.getMessage());
            throw new ExpiredTokenException();
        } catch (SignatureException e) {
            log.error("Signature Exception: {}", e.getMessage());
            throw new InvalidTokenException();
        } catch (JwtException e) {
            log.error("Jwt exception: {}", e.getMessage());
            throw new InvalidTokenException();
        }
    }

    public long getExpirationTime(final String token) {
        return parseClaims(token).getExpiration().getTime();
    }
}