package sv.ufg.ordenaenlinea.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Strings;
import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
//    private final Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Trata de obtener los datos de autorización
        String authorizationHeader = request.getHeader("Authorization");
        if (!Strings.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Process the JWT token and extract the claims
        String token = authorizationHeader.substring(7);
        Optional<Claims> body = jwtTokenUtil.getClaimsFromToken(token);
        if (body.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        // Set the username
        String username = body.get().getSubject();

        // Set the authorities
        var authorities = (List<Map<String, String>>) body.get().get("authorities");
        Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream()
                .map(m -> new SimpleGrantedAuthority(m.get("authority"))).collect(Collectors.toSet());

        // Create Spring Boot security authentication token
        Authentication authentication = new UsernamePasswordAuthenticationToken(username,
                null, simpleGrantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Pass the control to the next filter
        chain.doFilter(request, response);
    }
}
