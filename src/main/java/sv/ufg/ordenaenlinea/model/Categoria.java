package sv.ufg.ordenaenlinea.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.ufg.ordenaenlinea.request.CategoriaRequest;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "categoria_nombre_unq", columnNames = "nombre")
})
public class Categoria {
    @Id
    @SequenceGenerator(name = "categoria_id_seq", sequenceName = "categoria_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categoria_id_seq")
    private Integer id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @JsonIgnore
    private String urlImagen;

    public static Categoria of(CategoriaRequest categoriaRequest) {
        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setNombre(categoriaRequest.getNombre());
        return nuevaCategoria;
    }
}
