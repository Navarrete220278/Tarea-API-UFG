package sv.ufg.ordenaenlinea.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(
     uniqueConstraints = {
             @UniqueConstraint(name = "usuario_rol_unq", columnNames = {"usuario_id", "rol"})
     }
)
@Getter @Setter @NoArgsConstructor
public class UsuarioRol {
    @Id
    @SequenceGenerator(name = "usuario_rol_id_seq", sequenceName = "usuario_rol_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_rol_id_seq")
    @JsonIgnore
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;
}
