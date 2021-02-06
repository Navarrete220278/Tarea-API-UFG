package sv.ufg.ordenaenlinea.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sv.ufg.ordenaenlinea.model.Usuario;

@Getter
@Setter
@AllArgsConstructor
public class JwtAuthResponse {
    private String token;
    private Usuario usuario;
}
