package sv.ufg.ordenaenlinea.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Set;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "usuario_usuario_unq", columnNames = "usuario"),
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
    @Column(nullable = false)
    private String usuario;

    @NotBlank
    @Column(nullable = false)
    @Size(min = 6)
    @JsonIgnore
    private String password;

    @JsonIgnore
    private String urlImagen;

    @NotBlank
    @Email
    @Column(nullable = false)
    private String email;

    private String direccion;

    private String telefono;

    @NotNull
    @Column(nullable = false)
    private Boolean emailConfirmado;

    private Boolean telefonoConfirmado;

    @NotNull
    @Column(nullable = false)
    private Long versionToken;

    @OneToMany(mappedBy = "usuario")
    private Set<UsuarioRol> roles;

    @OneToMany(mappedBy = "usuario")
    @JsonIgnore
    private Set<Orden> ordenes;

    @OneToMany(mappedBy = "usuario")
    @JsonIgnore
    private Set<OrdenHistorial> historial;
}
