package sv.ufg.ordenaenlinea.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class JwtAuthController {
    private final JwtAuthService jwtAuthService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/auth/login")
    public JwtAuthResponse iniciarSesion(@Valid @RequestBody JwtAuthRequest jwtAuthRequest,
                                         HttpServletResponse response) {
        return jwtAuthService.iniciarSesion(jwtAuthRequest, response);
    }

    @PostMapping("/auth/refresh")
    @ResponseStatus(HttpStatus.CREATED)
    public JwtAuthResponse refrescarToken(HttpServletRequest request, HttpServletResponse response) {
        return jwtAuthService.refrescarToken(request, response);
    }

    @PostMapping("/auth/logout")
    public void cerrarSesion(HttpServletResponse response, Principal principal) {
        jwtAuthService.cerrarSesion(response, principal);
    }
}
