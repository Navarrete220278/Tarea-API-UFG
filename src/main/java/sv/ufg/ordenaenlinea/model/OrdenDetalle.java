package sv.ufg.ordenaenlinea.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Getter @Setter @NoArgsConstructor
public class OrdenDetalle {
    @Id
    @SequenceGenerator(name = "orden_detalle_id_seq", sequenceName = "orden_detalle_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orden_detalle_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orden_id", nullable = false)
    @JsonIgnore
    private Orden orden;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @NotNull
    @Min(value = 1)
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(nullable = false)
    private BigDecimal precio;
}
