package sv.ufg.ordenaenlinea.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter @NoArgsConstructor
public class UsuarioRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    private String nombres;

    @NotBlank
    private String apellidos;

    private String direccion;

    private String telefono;
}
