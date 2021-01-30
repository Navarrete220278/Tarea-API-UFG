package sv.ufg.ordenaenlinea.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orden_id")
    private Orden orden;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @NotNull
    @Column(nullable = false)
    private Estado estado;

    private String comentarios;
}
