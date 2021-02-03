package sv.ufg.ordenaenlinea.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Strings;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import sv.ufg.ordenaenlinea.model.Rol;
import sv.ufg.ordenaenlinea.model.Usuario;
import sv.ufg.ordenaenlinea.service.UsuarioService;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class JwtAuthService {
    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final Logger logger = LoggerFactory.getLogger(JwtAuthService.class);
    private final JwtTokenUtil jwtTokenUtil;

    public JwtAuthResponse iniciarSesion(JwtAuthRequest jwtAuthRequest,
                                         HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(jwtAuthRequest.getUsuario(), jwtAuthRequest.getPassword())
        );

        String token = jwtTokenUtil.generateToken(authentication.getName(), authentication.getAuthorities());

        // Crea el refresh token y lo guarda en una cookie HTTP only
        Long refreshTokenVersion = ((JwtUserDetails) authentication.getPrincipal()).getVersion();
        Cookie refreshTokenCookie = jwtTokenUtil.getRefreshTokenCookie(authentication.getName(), refreshTokenVersion);
        response.addCookie(refreshTokenCookie);

        return new JwtAuthResponse(token);
    }

    public JwtAuthResponse refrescarToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtTokenUtil.getRefreshTokenFromRequest(request);
        if (!Strings.hasText(refreshToken) || refreshToken == null)
            throw new BadCredentialsException("You did not provide a refresh token");

        Claims body = jwtTokenUtil.getClaimsFromRefreshToken(refreshToken).orElseThrow(
                () -> new BadCredentialsException("A refresh token was provided but it cannot be trusted")
        );

        // Leer detalles del usuario de la BD y verificar versión del token
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(Integer.parseInt(body.getSubject()));

            // Verificar la versión del token
            if (!Objects.equals(usuario.getVersionToken().toString(), body.get("version").toString())) {
                logger.error("Version de refresh token no válida. Actual: "
                        + usuario.getVersionToken() + ". Provista: " + body.get("version"));
                throw new BadCredentialsException("A refresh token was provided but it cannot be trusted");
            }

            // Generar un nuevo token
            Set<GrantedAuthority> authorities = Stream.of(new SimpleGrantedAuthority(Rol.CLIENTE.getRoleName()))
                    .collect(Collectors.toSet());
            if (usuario.getEsEmpleado()) authorities.add(new SimpleGrantedAuthority(Rol.EMPLEADO.getRoleName()));
            String token = jwtTokenUtil.generateToken(usuario.getId().toString(), authorities);

            // Emitir un nuevo refresh token (manteniendo el número de versión)
            Cookie refreshTokenCookie = jwtTokenUtil
                    .getRefreshTokenCookie(usuario.getId().toString(), usuario.getVersionToken());
            response.addCookie(refreshTokenCookie);

            return new JwtAuthResponse(token);
        } catch (EntityNotFoundException e) {
            logger.error("El refresh token es válido, pero el idUsuario " + body.getSubject() + " no existe");
            throw new BadCredentialsException("A refresh token was provided but it cannot be trusted");
        }
    }

    public void cerrarSesion(HttpServletResponse response, Principal principal) {
        // Incremento la version del refresh token en uno
        Integer idUsuario = Integer.parseInt(principal.getName());
        usuarioService.invalidarRefreshTokens(idUsuario);

        // Elimino la cookie de refresh token
        Cookie nullRefreshTokenCookie = jwtTokenUtil.getNullRefreshTokenCookie();
        response.addCookie(nullRefreshTokenCookie);
    }
}
