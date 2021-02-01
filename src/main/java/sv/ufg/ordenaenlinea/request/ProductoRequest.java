package sv.ufg.ordenaenlinea.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor
public class ProductoRequest {
    @NotBlank
    private String nombre;
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal precio;
}
