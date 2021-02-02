package sv.ufg.ordenaenlinea.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
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

    @OneToMany(mappedBy = "orden", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<OrdenDetalle> detalles = new HashSet<>();

    @OneToMany(mappedBy = "orden", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<OrdenHistorial> historial = new HashSet<>();
}
