package com.example.blog.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    // Header 의 KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";

    // Token 만료시간
    public final long TOKEN_TIME = 60 * 60 * 1000L; // 60분

    @Value("${jwt.secret.key}")
    // application.properties 의 secretKey 가져옴, Base64 Encode 한 값 넣음.
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        // Base64로 Encode 되어있는 secretKey 를 Decode 하여 사용
        key = Keys.hmacShaKeyFor(bytes);
        // 새로운 시크릿키 인스턴스 생성
    }

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    // 1. JWT(토큰생성)
    public String createToken(String username) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username) // 사용자 식별값(ID)
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 생성 시간에 대한 만료시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }

    // 2. JWT 토큰을 받아올때 - substring
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            // 토큰이 공백이 아닌 Bearer 로 시작할 때
            return tokenValue.substring(7); // 자르는 시작점
        }
        logger.error("토큰을 찾을 수 없습니다.");
        throw new NullPointerException("토큰을 찾을 수 없습니다.");
    }

    // 3. JWT 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(token); // key로 token 검증
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 4. JWT에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(token).getBody();
        // Jwt의 구조중 Payload(Body)부분에 토큰에 담긴 정보가 들어있다.
        // 정보의 한 조각을 클레임이라 부르고 key-value의 한 쌍으로 되어있음. 토큰에는 여러개의 클레임들을 넣을 수 있다.
    }
}
