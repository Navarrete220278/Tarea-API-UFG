package sv.ufg.ordenaenlinea.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.ufg.ordenaenlinea.request.UsuarioRequest;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "usuario_email_unq", columnNames = "email")
        }
)
@Getter @Setter @NoArgsConstructor
public class Usuario {
    @Id
    @SequenceGenerator(name = "usuario_id_seq", sequenceName = "usuario_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_id_seq")
    private Integer id;

    @NotBlank
    @Email
    @Column(nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    @Size(min = 6)
    @JsonIgnore
    private String password;

    @JsonIgnore
    private String urlImagen;

    private String direccion;

    private String telefono;

    @NotNull
    @Column(nullable = false)
    @JsonIgnore
    private Boolean emailConfirmado;

    @NotNull
    @Column(nullable = false)
    @JsonIgnore
    private Long versionToken;

    @NotNull
    @Column(nullable = false)
    private Boolean esEmpleado;

    @NotNull
    @Column(nullable = false)
    private Boolean inactivo;

    public static Usuario of(UsuarioRequest usuarioRequest) {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail(usuarioRequest.getEmail());
        nuevoUsuario.setPassword(usuarioRequest.getPassword());
        nuevoUsuario.setDireccion(usuarioRequest.getDireccion());
        nuevoUsuario.setTelefono(usuarioRequest.getTelefono());
        nuevoUsuario.setEmailConfirmado(false);
        nuevoUsuario.setInactivo(false);
        nuevoUsuario.setVersionToken(0L);
        nuevoUsuario.setEsEmpleado(false);

        return nuevoUsuario;
    }
}
