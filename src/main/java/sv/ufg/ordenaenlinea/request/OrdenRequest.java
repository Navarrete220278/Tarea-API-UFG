package sv.ufg.ordenaenlinea.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor
public class OrdenRequest {

    @Getter @Setter @NoArgsConstructor
    public static class Linea {
        @NotNull
        private Integer idProducto;
        @NotNull
        private Integer cantidad;
    }

    private LocalDateTime fechaSolicitada;
    private List<Linea> lineas = new ArrayList<>();
}
