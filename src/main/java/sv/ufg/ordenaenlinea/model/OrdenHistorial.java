package sv.ufg.ordenaenlinea.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(name = "orden_estado_unq", columnNames = {
                "orden_id", "estado"
        })
)
@Getter
@Setter
@NoArgsConstructor
public class OrdenHistorial {
    @Id
    @SequenceGenerator(name = "orden_historial_id_seq", sequenceName = "orden_historial_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orden_historial_id_seq")
    @JsonIgnore
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orden_id")
    @JsonIgnore
    private Orden orden;

    @Column(nullable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime fecha;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private String comentarios;
}
