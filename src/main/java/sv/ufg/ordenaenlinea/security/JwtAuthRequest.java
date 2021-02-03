package sv.ufg.ordenaenlinea.security;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter @NoArgsConstructor
public class JwtAuthRequest {
    @NotBlank
    private String usuario;
    @NotBlank
    private String password;
}
