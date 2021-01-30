package sv.ufg.ordenaenlinea.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
public class Orden {
    @Id
    @SequenceGenerator(name = "orden_id_seq", sequenceName = "orden_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orden_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonBackReference
    private Usuario usuario;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime fechaCreada;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime fechaEntrega;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado;

    @OneToMany(mappedBy = "orden")
    private Set<OrdenDetalle> detalles;

    @OneToMany(mappedBy = "orden")
    private Set<OrdenHistorial> historial;
}
