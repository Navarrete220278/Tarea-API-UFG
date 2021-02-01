package sv.ufg.ordenaenlinea.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class CategoriaRequest {
    @NotBlank
    private String nombre;
}
