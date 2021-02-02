package sv.ufg.ordenaenlinea.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor @Getter
public enum Estado {
    PENDIENTE(10),
    CANCELADA(20),
    PREPARACION(30),
    EN_TRANSITO(40),
    ENTREGADA(50);

    private final Integer secuencia;

    // Estado por defecto para nuevas ordenes
    public static Estado getEstadoPorDefecto() {
        return Estado.PENDIENTE;
    }

    // Estados inmutables: no pueden modificarse por otro estado
    public Boolean esInmutable() {
        return Arrays.asList(Estado.CANCELADA, Estado.ENTREGADA).contains(this);
    }
}
