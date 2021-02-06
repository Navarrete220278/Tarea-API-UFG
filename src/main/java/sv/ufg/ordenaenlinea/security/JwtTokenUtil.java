package sv.ufg.ordenaenlinea.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenUtil {
    @Value("${app.security.jwt.refresh-token.secret}")
    private String tokenSecret;

    @Value("${app.security.jwt.token.minutes}")
    private Long tokenMinutes;

    @Value("${app.security.jwt.refresh-token.secret}")
    private String refreshTokenSecret;

    @Value("${app.security.jwt.refresh-token.days}")
    private Long refreshTokenDays;

    @Value("${app.security.jwt.refresh-token.cookie-name}")
    private String refreshTokenCookieName;

    private final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    public Optional<Claims> getClaimsFromToken(String token) {
        try {
            return Optional.of(Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(tokenSecret.getBytes()))
                    .build().parseClaimsJws(token).getBody());
        } catch (JwtException ex) {
            logger.error(ex.getMessage() + " Token: " + token + ".");
            return Optional.empty();
        }
    }

    public String generateToken(String subject, Object authorities) {
        return Jwts.builder().setSubject(subject)
                .claim("authorities", authorities)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * tokenMinutes))
                .signWith(Keys.hmacShaKeyFor(tokenSecret.getBytes()))
                .compact();
    }

    private String generateRefreshToken(String subject, Long version) {
        return Jwts.builder().setSubject(subject)
                .claim("version", version)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * refreshTokenDays))
                .signWith(Keys.hmacShaKeyFor(refreshTokenSecret.getBytes()))
                .compact();
    }

    public Optional<Claims> getClaimsFromRefreshToken(String refreshToken) {
        try {
            return Optional.of(Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(refreshTokenSecret.getBytes()))
                    .build().parseClaimsJws(refreshToken).getBody());
        } catch (JwtException ex) {
            logger.error(ex.getMessage() + " Refresh token: " + refreshToken + ".");
            return Optional.empty();
        }
    }

    private Cookie getCookie(String refreshToken) {
        Cookie cookie = new Cookie(refreshTokenCookieName, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/api/v1/auth/refresh");
        cookie.setMaxAge(60 * 60 * 24 * refreshTokenDays.intValue());

        return cookie;
    }

    public Cookie getRefreshTokenCookie(String subject, Long version) {
        String refreshToken = generateRefreshToken(subject, version);
        return getCookie(refreshToken);
    }

    public Cookie getNullRefreshTokenCookie() {
        return getCookie(null);
    }

    public String getRefreshTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals(refreshTokenCookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
